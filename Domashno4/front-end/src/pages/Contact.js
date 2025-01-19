import React, {useState} from "react";
import "./Contact.css";
import {useTranslation} from "react-i18next";
import emailjs from "emailjs-com";

function Contact(){

    const{t}=useTranslation();

    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const sendEmail = (e) => {
        e.preventDefault();

        if (!email || !message) {
            setError("Email and message are required!");
            return;
        }

        emailjs.send(
            "service_v21dt5s",
            "template_t4x4np3",
            { from_name:email, message },
            "kzbN-ZcjE8betas9I"
        ).then((res) => {
            alert("Email sent successfully!");
            setEmail("");
            setMessage("");
        }).catch((err) => {
            setError("Email not sent!");
        });
    };

    return(
        <div className="contact">
            <h1>{t("contact-page-title")}</h1>
           {/* <h3>{t("contact-page-h1")}</h3>*/}
            <form onSubmit={sendEmail}>
                <label htmlFor="email-input">{t("contact-page-h1")}</label>
                <input
                    type="email"
                    id="email-input"
                    placeholder={t("contact-page-first-input")}
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />

                <label htmlFor="text-field">{t("contact-page-second-input")}</label>
                <textarea
                    id="text-field"
                    placeholder={t("contact-page-second-input")}
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    required
                ></textarea>

                {error && <p className="error-message">{error}</p>}

                <button type="submit" id="send-email">{t("contact-page-input-btn")}</button>
            </form>

            {/*<form>
                <input type="email" id="email-input" placeholder={t("contact-page-first-input")}/>
                <input type="text" id="text-field" placeholder={t("contact-page-second-input")}/>
                <button id="send-email">{t("contact-page-input-btn")}</button>
            </form>*/}

        </div>
    )
}

export default Contact;