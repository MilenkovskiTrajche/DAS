import './App.css';
import Home from "./pages/Home";
import React, {useEffect, useState} from "react";
import {BrowserRouter as Router, Routes, Route, useLocation} from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Data from "./pages/Data";
import Contact from "./pages/Contact";
import About from "./pages/About";
import axios from "axios";
import ScraperFrontPage from "./pages/ScraperFrontPage";

function App() {

    const [data,setData]=useState("");
    const location=useLocation();

    useEffect(()=>{
        axios.get("http://localhost:8080/api/stocks/data")
            .then(response=>{
                setData(response.data);
            }).catch(error=>{
            console.error("There was an error",error);
        });
    },[]);

    const isScraperPage=location.pathname==="/";

    return (
        /*
              <Router>
        */
        <div className="App">
            { !isScraperPage && <Navbar/>}
            {/*
              <h1>Spring:{data}</h1>
*/}
            <Routes>
                <Route path="/" element={<ScraperFrontPage/>}/>
                <Route path="/home" element={<Home/>}/>
                <Route path="/data" element={<Data/>}/>
                <Route path="/contact" element={<Contact/>}/>
                <Route path="/about" element={<About/>}/>
            </Routes>
            {!isScraperPage && <Footer/>}
        </div>
        /*
              </Router>
        */

    );
}

export default App;
