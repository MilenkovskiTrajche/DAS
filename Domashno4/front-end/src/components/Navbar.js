import React, {useTransition} from "react";
import {Link} from "react-router-dom";
import "./i18n";
import i18next from "i18next";
import {useTranslation} from "react-i18next";
import "./Navbar.css";

function Navbar(){

    const {t,i18n}=useTranslation();

    const changeLanguage=(lang)=>{
        i18n.changeLanguage(lang)
        localStorage.setItem("language",lang);
    };

    return(
        <div className="navbar">
            <div className="nav-links">
                <nav>
                    <ul>
                        <li><Link to="/home">{t("nav-home-header")}</Link></li>
                        <li><Link to="/data">{t("nav-data-header")}</Link></li>
                        <li><Link to="/contact">{t("nav-contact-header")}</Link></li>
                        <li><Link to="/about">{t("nav-about-header")}</Link></li>
                    </ul>
                </nav>
            </div>
            <div className="language-buttons">
                <button id="macedonian" onClick={()=>changeLanguage("mk")}><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/Flag_of_North_Macedonia.svg/383px-Flag_of_North_Macedonia.svg.png" alt="macedonian-flag"/></button>
                <button id="english" onClick={()=>changeLanguage("en")}><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Flag_of_the_United_Kingdom_(1-2).svg/383px-Flag_of_the_United_Kingdom_(1-2).svg.png" alt="british-flag"/></button>
            </div>
        </div>
    )
}
export default Navbar;