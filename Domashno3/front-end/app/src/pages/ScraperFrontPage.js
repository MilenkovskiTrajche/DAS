import React, {useEffect, useState} from 'react'
import "./ScraperFrontpage.css"
import axios from "axios";

export default function ScraperFrontPage(){
    const [executionTime,setExecutionTime]=useState(null);
    const [buttonEnabled,setButtonEnabled]=useState(false);
    const [scrapingInProgress,setScrapingInProgress]=useState(false);

    const handleScraping=async ()=>{
        try{
            setScrapingInProgress(true);
            setButtonEnabled(false);
            setExecutionTime(null);

            const response=await  axios.post("http://localhost:8080/api/stocks/scraper");

            if(response.status===200){
                setExecutionTime(response.data.execution_time);
                setButtonEnabled(true);
                alert("Scraping started successfully.Please wait around 3-5 minutes!")
            }
        }catch (error){
            console.error("Error during scraping:",error);
            alert("Scraping failed")
        } finally {
            setScrapingInProgress(false);
        }
    };

    const handleGoToHomePage=()=>{
        window.location.href='/home';
    }

    return(
        <div className="scraper">
            <h1>This is the scraper</h1>
            <button id="btn-start-scraper" onClick={handleScraping}  disabled={scrapingInProgress}>
                Start the scraper
            </button>
            {executionTime &&(
                <div className="scraper-time">
                    <h5>Scraping complete!</h5>
                    <h5>Execution time:{executionTime}</h5>
                </div>
            )}
            {buttonEnabled && (
                <button id="btn-main-page" onClick={handleGoToHomePage} disabled={!buttonEnabled}>Go to main page</button>
            )}

        </div>
    )
}
