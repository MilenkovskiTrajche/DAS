import React from "react";
import "./Home.css";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import stocksImage from "../assets/images/stocks_image.png";

function Home(){
    return(
        <div className="home">
            <div className="main">
                <div className="image-container">
                    <img src={stocksImage} alt="stocks-image"/>
                    <h1>Податоците од берзата<br/>за секој ден<br/>во последните<br/>10 години</h1>
                </div>
            </div>
        </div>
    )
}

export default Home;