import React, { useEffect, useState } from "react";
import "./Data.css";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import axios from "axios";

function Data() {
    const [symbols, setSymbols] = useState([]);
    const [selectedSymbol, setSelectedSymbol] = useState("");
    const [selectedIndicator, setSelectedIndicator] = useState("");
    const [selectedPeriod, setSelectedPeriod] = useState("");

    const [movingAverages, setMovingAverages] = useState([]);
    const [movingAveragesSymbol, setMovingAveragesSymbol] = useState("");
    const [movingAveragesDate, setMovingAveragesDate] = useState("");

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

        /*if (selectedIndicator) {
            url = selectedIndicator === "moving-averages"
                ? "http://localhost:8080/api/moving-averages/search"
                : "http://localhost:8080/api/oscillators/search";
        }*/

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
                            <label htmlFor="company">Одберете компанија:</label>
                            <select
                                id="company"
                                value={selectedSymbol}
                                onChange={(e) => setSelectedSymbol(e.target.value)}
                            >
                                <option value="">Одбери симбол</option>
                                {symbols.map((symbol) => (
                                    <option key={symbol} value={symbol}>
                                        {symbol}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="single-filters">
                            <label htmlFor="date">Одберете индикатор:</label>
                            <select
                                id="date"
                                value={selectedIndicator}
                                onChange={(e) => setSelectedIndicator(e.target.value)}
                            >
                                <option value="">Одбери индикатор</option>
                                <option value="moving-averages">Moving Averages</option>
                                <option value="oscillators">Осцилатори</option>
                            </select>
                        </div>

                        <div className="single-filters">
                            <label htmlFor="type">Одберете период:</label>
                            <select
                                id="type"
                                value={selectedPeriod}
                                onChange={(e) => setSelectedPeriod(e.target.value)}
                            >
                                <option value="">Одбери период</option>
                                <option value="weekly">Неделно</option>
                                <option value="monthly">Месечно</option>
                            </select>
                        </div>

                        <button id="show" type="submit">Прикажете</button>
                    </div>
                </form>
                <div className="table">
                    <h1>Индикатори за симбол {movingAveragesSymbol}</h1>
                    <table>
                        <thead>
                        <tr>
                            <th>Датум</th>
                            <th>Сигнал</th>
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
                                <td colSpan="2">Нема податоци</td>
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
