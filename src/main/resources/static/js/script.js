// Select elements
const form = document.getElementById("urlForm");
const originalUrlInput = document.getElementById("originalUrl");
const shortCodeInput = document.getElementById("shortCode");
const passwordInput = document.getElementById("password");
const expiryDateInput = document.getElementById("expiryDate");
const errorMessage = document.getElementById("errorMessage");
const successFeedback = document.getElementById("successFeedback");
const toggleAdvanced = document.getElementById("toggleAdvanced");
const advancedOptions = document.getElementById("advancedOptions");
const linksList = document.getElementById("linksList");

// Toggle Advanced Options
toggleAdvanced.addEventListener("click", function () {
    const isExpanded = advancedOptions.classList.contains("expanded");
    if (isExpanded) {
        advancedOptions.classList.remove("expanded");
        advancedOptions.classList.add("collapsed");
        this.classList.remove("active");
    } else {
        advancedOptions.classList.remove("collapsed");
        advancedOptions.classList.add("expanded");
        this.classList.add("active");
    }
});

// Load Dashboard Data (Metrics and Link list)
async function loadDashboard() {
    try {
        const response = await fetch("/api/dashboard");
        if (!response.ok) throw new Error("Failed to load dashboard data");
        
        const data = await response.json();
        
        // Update Metrics
        document.getElementById("metricActiveLinks").innerText = data.metrics.activeLinks;
        document.getElementById("metricTotalClicks").innerText = data.metrics.totalClicks;
        document.getElementById("metricAverageClicks").innerText = data.metrics.averageClicks;
        
        // Update Link List
        if (data.links.length === 0) {
            linksList.innerHTML = `<p class="empty-state">No links shortened yet.</p>`;
            return;
        }
        
        linksList.innerHTML = data.links.map(link => {
            const shortUrl = `${window.location.origin}/${link.shortCode}`;
            return `
                <div class="link-item">
                    <div class="link-main-row">
                        <div class="link-details">
                            <a href="${shortUrl}" target="_blank" class="short-url-link">${shortUrl}</a>
                            <span class="original-url-text" title="${link.originalUrl}">${link.originalUrl}</span>
                        </div>
                        <div class="link-stats">
                            <div class="click-counter">
                                <svg class="click-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                                </svg>
                                <span>${link.clickCount} clicks</span>
                            </div>
                            <div class="link-actions-group">
                                <button type="button" class="action-btn copy-btn" data-url="${shortUrl}">
                                    📋 Copy
                                </button>
                                <button type="button" class="action-btn qr-btn" data-url="${shortUrl}">
                                    📱 QR Code
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="link-qr-container" style="display: none;">
                        <img src="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${encodeURIComponent(shortUrl)}" alt="QR Code" width="150" height="150">
                    </div>
                </div>
            `;
        }).join("");
        
    } catch (error) {
        console.error("Dashboard load error:", error);
        linksList.innerHTML = `<p class="empty-state" style="color: #ef4444;">Failed to load links dashboard.</p>`;
    }
}

// Event Delegation for Copy & QR code buttons in the list
linksList.addEventListener("click", function (event) {
    // Copy action
    const copyButton = event.target.closest(".copy-btn");
    if (copyButton) {
        const urlToCopy = copyButton.getAttribute("data-url");
        navigator.clipboard.writeText(urlToCopy);
        const originalText = copyButton.innerHTML;
        copyButton.innerHTML = "✅ Copied";
        setTimeout(() => {
            copyButton.innerHTML = originalText;
        }, 2000);
        return;
    }

    // QR Code toggle action
    const qrButton = event.target.closest(".qr-btn");
    if (qrButton) {
        const linkItem = qrButton.closest(".link-item");
        const qrContainer = linkItem.querySelector(".link-qr-container");
        if (qrContainer.style.display === "none") {
            qrContainer.style.display = "flex";
        } else {
            qrContainer.style.display = "none";
        }
        return;
    }
});

// Form Submission
form.addEventListener("submit", async function (event) {
    event.preventDefault();

    // Reset previous states
    errorMessage.style.display = "none";
    errorMessage.innerText = "";
    successFeedback.style.display = "none";

    const originalUrl = originalUrlInput.value;
    const shortCode = shortCodeInput.value;
    const password = passwordInput.value;

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
            // Show "Created!" feedback message below the button
            successFeedback.style.display = "block";
            
            // Clear input fields
            originalUrlInput.value = "";
            shortCodeInput.value = "";
            passwordInput.value = "";
            expiryDateInput.value = "";
            
            // Collapse Advanced Options if open
            advancedOptions.classList.remove("expanded");
            advancedOptions.classList.add("collapsed");
            toggleAdvanced.classList.remove("active");

            // Refresh Dashboard data dynamically
            await loadDashboard();
            
            // Automatically hide success feedback banner after 4 seconds
            setTimeout(() => {
                successFeedback.style.display = "none";
            }, 4000);
        } else {
            errorMessage.innerText = data.error || "Failed to create short URL.";
            errorMessage.style.display = "block";
        }
    } catch (error) {
        errorMessage.innerText = "An unexpected error occurred. Please try again.";
        errorMessage.style.display = "block";
        console.error("Form submit error:", error);
    }
});

// Initial dashboard load on Page Load
document.addEventListener("DOMContentLoaded", () => {
    loadDashboard();
});