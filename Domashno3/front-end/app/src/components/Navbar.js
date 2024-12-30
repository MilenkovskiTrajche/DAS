import "./Navbar.css"
import React from "react";
import {Link} from "react-router-dom";

function Navbar(){
    return(
        <div className="navbar">
            <div className="nav-links">
                <nav>
                    <ul>
                        <li><Link to="/home">Дома</Link></li>
                        <li><Link to="/data">Податоци</Link></li>
                        <li><Link to="/contact">Контакт</Link></li>
                        <li><Link to="/about">За нас</Link></li>
                    </ul>
                </nav>
            </div>
            <div className="language-buttons">
                <button id="macedonian"><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/Flag_of_North_Macedonia.svg/383px-Flag_of_North_Macedonia.svg.png" alt="macedonian-flag"/></button>
                <button id="english"><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Flag_of_the_United_Kingdom_%281-2%29.svg/383px-Flag_of_the_United_Kingdom_%281-2%29.svg.png" alt="british-flag"/></button>
            </div>
        </div>
    )
}

export default Navbar;