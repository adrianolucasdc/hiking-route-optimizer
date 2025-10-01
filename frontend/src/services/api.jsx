// frontend/src/services/api.jsx
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080", // porta padrão de apps Java Spring Boot
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
