import React from "react";
import "./Home.css";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import stocksImage from "../assets/images/stocks_image.png";
import {useTranslation} from "react-i18next";

function Home(){

    const{t}=useTranslation()

    return(
        <div className="home">
            <div className="main">
                <div className="image-container">
                    <img src={stocksImage} alt="stocks-image"/>
                    <h1>{t("home-page-text")}</h1>
                </div>
            </div>
        </div>
    )
}

export default Home;