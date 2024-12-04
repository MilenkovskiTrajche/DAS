import React from "react";
import "./Contact.css";

function Contact(){
    return(
        <div className="contact">
            <h1>Контактирајте не преку е-маил</h1>
            <h3>Внесете го вашиот e-маил тука:</h3>
            <input type="email" id="email-input" placeholder="e-mail"/>
            <input type="text" id="text-field" placeholder="Внесете ја вашата порака тука"/>
            <button id="send-email">Send</button>
        </div>
    )
}

export default Contact;