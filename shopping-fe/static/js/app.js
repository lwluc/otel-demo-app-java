async function submitForm(e, form) {
    showProgressbar();
    e.preventDefault();
    const jsonFormData = buildJsonFormData(form);
    const urlSearchParams = new URLSearchParams(window.location.search);
    const params = Object.fromEntries(urlSearchParams.entries());
    const url = `http://localhost:50050/shop/order?${urlSearchParams.toString()}`;
    try {
        const responseStatus = await performPostHttpRequest(url, jsonFormData);
        if (responseStatus < 200 || responseStatus > 299) {
            showNotification('An error occurred.', 'is-danger');
        } else {
            showNotification('Order is placed', 'is-primary');
        }
    } catch(error) {
        showNotification('An error occurred.', 'is-danger');
    } finally {
        removeProgressbar();
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

function showNotification(text, typeClass) {
  const element = document.getElementById('snackbar');
  const msgEl = document.createTextNode(text);
  element.appendChild(msgEl);

  element.classList.add('show');
  element.classList.add(typeClass);

  setTimeout(function() {
    element.className = element.className.replace('show', '');
    element.removeChild(msgEl);
    }, 1500);
}

function showProgressbar() {
  const progressbar = document.getElementById('progress');
  progressbar.classList.add('show');
}

function removeProgressbar() {
  const progressbar = document.getElementById('progress');
  progressbar.className = progressbar.className.replace('show', '');
}

const orderForm = document.querySelector('#orderForm');
if(orderForm) {
    orderForm.addEventListener('submit', (e) => submitForm(e, orderForm));
}