import axios from "axios";

const python_api = axios.create({
  baseURL: "http://localhost:5000", 
  headers: {
    "Content-Type": "application/json",
  },
});

export default python_api;
