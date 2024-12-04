import React from "react";
import "./Data.css";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";

function Data(){
    return(
        <div className="data">
            <div className="data-main">
                <div className="filters">
                    <div className="single-filters">
                        <label htmlFor="company">Одберете компанија:</label>
                        <select id="company">
                            <option>ALK</option>
                            <option>NLB</option>
                            <option>TTK</option>
                        </select>
                    </div>
                    <div className="single-filters">
                        <label htmlFor="date">Датум:</label>
                        <select id="date">
                            <option>01.01.2023</option>
                            <option>02.02.2022</option>
                            <option>03.03.2021</option>
                        </select>
                    </div>
                    <div className="single-filters">
                        <label htmlFor="type">Прикажи:</label>
                        <select id="type">
                            <option>Најпрофитабилни</option>
                            <option>Најисплатливи</option>
                            <option>Најевтини</option>
                        </select>
                    </div>
                    <button id="compare">Споредете 2 кодови</button>
                    <button id="show">Прикажете</button>
                    <button id="download">Превземете</button>
                    <button id="download-all">Превземете ги сите</button>
                </div>
                <div className="table">
                    <table>
                        <thead>
                        <th>Датум</th>
                        <th>Цена на<br/>последна<br/>трансакција</th>
                        <th>Мак.</th>
                        <th>Мин.</th>
                        <th>Просечна цена</th>
                        <th>Пром.%</th>
                        <th>Количина</th>
                        <th>Промет во БЕСТ во денари</th>
                        <th>Вкупен промет во денари</th>
                        </thead>
                        <tbody>
                        <tr><td>01.01.2022</td><td>25.649,00</td><td>25.650,00</td><td>25.650,00</td><td>25.581,37</td><td>3,89</td><td>339</td><td>8.672.085</td><td>8.672.085</td></tr>
                        <tr><td>01.01.2022</td><td>25.649,00</td><td>25.650,00</td><td>25.650,00</td><td>25.581,37</td><td>3,89</td><td>339</td><td>8.672.085</td><td>8.672.085</td></tr>
                        <tr><td>01.01.2022</td><td>25.649,00</td><td>25.650,00</td><td>25.650,00</td><td>25.581,37</td><td>3,89</td><td>339</td><td>8.672.085</td><td>8.672.085</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    )
}

export default Data;