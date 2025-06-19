const express = require("express");
const cors = require("cors");
const sqlite = require("sqlite");
const sqlite3 = require("sqlite3");
const path = require("path");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const axios = require("axios");
const { v4: uuidv4 } = require("uuid");

const app = express();
app.use(cors());
app.use(express.json());

const dbPath = path.join(__dirname, "auth.db");
let db;

// ------------------ DB INIT ------------------
const initServerAndDb = async () => {
  try {
    db = await sqlite.open({
      filename: dbPath,
      driver: sqlite3.Database,
    });

    // Optional: Create table if not exists
    await db.run(`
      CREATE TABLE IF NOT EXISTS users (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        email TEXT UNIQUE NOT NULL,
        password TEXT NOT NULL,
        role TEXT DEFAULT 'CUSTOMER'
      )
    `);

    app.listen(8085, () => {
      console.log("Auth service running on port 8085");
    });
  } catch (error) {
    console.error("Failed to start DB:", error.message);
    process.exit(1);
  }
};
initServerAndDb();

// ------------------ JWT MIDDLEWARE ------------------
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers["authorization"];
  const token = authHeader?.split(" ")[1];

  if (!token) return res.status(401).send({ message: "Token required" });

  jwt.verify(token, "MY_TOKEN", (err, payload) => {
    if (err) return res.status(401).send({ message: "Invalid token" });
    req.email = payload.email;
    next();
  });
};

// ------------------ SIGNUP ------------------
app.post("/auth/register", async (req, res) => {
  const { name, email, password, role = "CUSTOMER", phone } = req.body;

  try {
    const userExists = await db.get(`SELECT * FROM users WHERE email = ?`, [
      email,
    ]);
    if (userExists)
      return res.status(400).send({ message: "User already exists" });

    const hashedPwd = await bcrypt.hash(password, 10);
    const userId = uuidv4();

    await db.run(
      `INSERT INTO users (id, name, email, password, role, phone) VALUES (?, ?, ?, ?, ?, ?)`,
      [userId, name, email, hashedPwd, role, phone]
    );

    // After user is saved...
    if (role.toUpperCase() === "CUSTOMER") {
      try {
        await axios.post("http://localhost:8080/customer-service/customers", {
          name,
          email,
          phone,
          userId: userId, // Optional: If customer-service links to auth user
        });
        console.log("Customer created in customer-service");
      } catch (err) {
        console.error("Failed to create customer:", err.message);
      }
    }

    res.status(201).send({ message: "User created successfully" });
  } catch (err) {
    console.error(err);
    res.status(500).send({ message: "Signup failed" });
  }
});

// ------------------ LOGIN ------------------
app.post("/auth/login", async (req, res) => {
  const { email, password } = req.body;

  try {
    const user = await db.get(`SELECT * FROM users WHERE email = ?`, [email]);
    if (!user) return res.status(400).send({ message: "Invalid credentials" });

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch)
      return res.status(400).send({ message: "Invalid credentials" });

    const token = jwt.sign({ email: user.email, role: user.role }, "MY_TOKEN");
    res.send({ jwt_token: token });
  } catch (err) {
    console.error(err);
    res.status(500).send({ message: "Login failed" });
  }
});

const eurekaClient = require("./eureka");

eurekaClient.start((error) => {
  if (error) {
    console.log("Failed to register with Eureka:", error);
  } else {
    console.log("Registered with Eureka");
  }
});
