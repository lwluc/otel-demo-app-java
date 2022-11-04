async function submitForm(e, form) {
    e.preventDefault();
    const jsonFormData = buildJsonFormData(form);
    const urlSearchParams = new URLSearchParams(window.location.search);
    const params = Object.fromEntries(urlSearchParams.entries());
    const url = `http://localhost:50050/shop/order?${urlSearchParams.toString()}`;
    const responseStatus = await performPostHttpRequest(url, jsonFormData);
    if (responseStatus < 200 || responseStatus > 299) {
        alert(`An error occured.`);
    }
}

function buildJsonFormData(form) {
    const jsonFormData = {};
    new FormData(form).forEach((value, key) => jsonFormData[key] = value);
    return jsonFormData;
}

async function performPostHttpRequest(fetchLink, body) {
    if (!fetchLink || !body) {
        throw new Error('One or more POST request parameters was not passed.');
    }
    const headers = { "Content-Type": "application/json" };
    try {
        const rawResponse = await fetch(fetchLink, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(body)
        });
        return await rawResponse.status;
    }
    catch(err) {
        console.error(`Error at fetch POST: ${err}`);
        throw err;
    }
}

const orderForm = document.querySelector('#orderForm');
if(orderForm) {
    orderForm.addEventListener('submit', (e) => submitForm(e, orderForm));
}