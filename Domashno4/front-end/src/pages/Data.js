import React, { useEffect, useState } from "react";
import "./Data.css";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import axios from "axios";
import {useTranslation} from "react-i18next";

function Data() {
    const [symbols, setSymbols] = useState([]);
    const [selectedSymbol, setSelectedSymbol] = useState("");
    const [selectedIndicator, setSelectedIndicator] = useState("");
    const [selectedPeriod, setSelectedPeriod] = useState("");

    const [movingAverages, setMovingAverages] = useState([]);
    const [movingAveragesSymbol, setMovingAveragesSymbol] = useState("");

    //for the translation
    const{t}=useTranslation()

    // Fetch symbols from the backend
    useEffect(() => {
        const fetchSymbols = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/stocks/symbols");
                setSymbols(response.data);
            } catch (error) {
                console.error("Error fetching symbols:", error);
            }
        };

        fetchSymbols();
    }, []);

    // Handle form submission
    const handleSubmit = async (event) => {
        event.preventDefault();
        let url = "";

        if(selectedIndicator==='moving-averages'){
            url="http://localhost:8080/api/moving-averages/search";
        }else {
            url="http://localhost:8080/api/oscillators/search";
        }
        try {
            // Adjust to use query parameters for the GET request
            const response = await axios.get(url, {
                params: {
                    symbol: selectedSymbol,
                    type: selectedPeriod,
                },
            });
            console.log("Response:", response);
            setMovingAverages(response.data); // Update the table data
            setMovingAveragesSymbol(selectedSymbol); // Update the displayed symbol
        } catch (error) {
            console.log("Error submitting the form:", error);
        }
    };

    return (
        <div className="data">
            <div className="data-main">
                <form className="form-filters" onSubmit={handleSubmit}>
                    <div className="filters">
                        <div className="single-filters">
                            <label htmlFor="company">{t("data-page-first-filter")}</label>
                            <select
                                id="company"
                                value={selectedSymbol}
                                onChange={(e) => setSelectedSymbol(e.target.value)}
                            >
                                <option value="">{t("data-page-first-filter-option")}</option>
                                {symbols.map((symbol) => (
                                    <option key={symbol} value={symbol}>
                                        {symbol}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="single-filters">
                            <label htmlFor="date">{t("data-page-second-filter")}</label>
                            <select
                                id="date"
                                value={selectedIndicator}
                                onChange={(e) => setSelectedIndicator(e.target.value)}
                            >
                                <option value="">{t("data-page-second-filter-option")}</option>
                                <option value="moving-averages">{t("data-page-second-filter-first-option")}</option>
                                <option value="oscillators">{t("data-page-second-filter-second-option")}</option>
                            </select>
                        </div>

                        <div className="single-filters">
                            <label htmlFor="type">{t("data-page-third-filter")}</label>
                            <select
                                id="type"
                                value={selectedPeriod}
                                onChange={(e) => setSelectedPeriod(e.target.value)}
                            >
                                <option value="">{t("data-page-third-filter-option")}</option>
                                <option value="weekly">{t("data-page-third-filter-first-option")}</option>
                                <option value="monthly">{t("data-page-third-filter-second-option")}</option>
                            </select>
                        </div>

                        <button id="show" type="submit">{t("data-page-display-data-btn")}</button>
                    </div>
                </form>
                <div className="table">
                    <h1>{t("data-page-table-header")} {movingAveragesSymbol}</h1>
                    <table>
                        <thead>
                        <tr>
                            <th>{t("data-page-table-first-column-header")}</th>
                            <th>{t("data-page-table-second-column-header")}</th>
                        </tr>
                        </thead>
                        <tbody>
                        {movingAverages.length > 0 ? (
                            movingAverages.map((ma) => (
                                <tr key={ma.id}>
                                    <td>{ma.date}</td>
                                    <td>{ma.signal}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="2">{t("data-page-table-no-data")}</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default Data;
