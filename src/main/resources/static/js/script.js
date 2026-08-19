const form = document.getElementById("urlForm");
const resultCard = document.getElementById("resultCard");
const generatedUrlInput = document.getElementById("generatedUrl");
const copyBtn = document.getElementById("copyBtn");
const qrBtn = document.getElementById("qrBtn");
const qrContainer = document.getElementById("qrContainer");
const qrImage = document.getElementById("qrImage");
const errorMessage = document.getElementById("errorMessage");
const successFeedback = document.getElementById("successFeedback");

// Add static event listener to copy button
copyBtn.addEventListener("click", function () {
    const textToCopy = generatedUrlInput.value;
    if (textToCopy) {
        navigator.clipboard.writeText(textToCopy);
        this.innerHTML = "✅ Copied";
        setTimeout(() => {
            this.innerHTML = "📋 Copy";
        }, 2000);
    }
});

// Toggle QR Code visibility
qrBtn.addEventListener("click", function () {
    if (qrContainer.style.display === "none") {
        qrContainer.style.display = "block";
    } else {
        qrContainer.style.display = "none";
    }
});

form.addEventListener("submit", async function (event) {
    event.preventDefault();

    const originalUrl = document.getElementById("originalUrl").value;
    const shortCode = document.getElementById("shortCode").value;
    const password = document.getElementById("password").value;
    const expiryDate = document.getElementById("expiryDate").value;

    // Reset previous states
    errorMessage.style.display = "none";
    errorMessage.innerText = "";
    resultCard.style.display = "none";
    qrContainer.style.display = "none";
    successFeedback.style.display = "none";

    try {
        const response = await fetch("/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                originalUrl: originalUrl,
                shortCode: shortCode,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok) {
            generatedUrlInput.value = data.shortUrl;
            resultCard.style.display = "block";
            
            // Set QR code source and display container
            qrImage.src = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="
                + encodeURIComponent(data.shortUrl);
            qrContainer.style.display = "block";
            
            // Display "Created!" text below the button
            successFeedback.style.display = "block";
        } else {
            errorMessage.innerText = data.error || "Failed to create short URL.";
            errorMessage.style.display = "block";
        }
    } catch (error) {
        errorMessage.innerText = "An unexpected error occurred. Please try again.";
        errorMessage.style.display = "block";
        console.error(error);
    }
});