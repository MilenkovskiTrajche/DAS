import './App.css';
import Home from "./pages/Home";
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Data from "./pages/Data";
import Contact from "./pages/Contact";
import About from "./pages/About";

function App() {
  return (
      <Router>
          <div className="App">
              <Navbar/>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/data" element={<Data/>}/>
                    <Route path="/contact" element={<Contact/>}/>
                    <Route path="/about" element={<About/>}/>
                </Routes>
              <Footer/>
          </div>
      </Router>

  );
}

export default App;
