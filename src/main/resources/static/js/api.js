// fetch wrapper: agrega el header Authorization con el JWT automaticamente
// Redirige al login si el servidor responde 401 o 403

async function apiFetch(path, options) {
    options = options || {};

    var token = getToken();
    var headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }
    if (options.headers) {
        Object.assign(headers, options.headers);
    }

    var res = await fetch(API_BASE + path, Object.assign({}, options, { headers: headers }));

    if (res.status === 401 || res.status === 403) {
        clearSession();
        window.location.replace('/login.html');
        return null;
    }

    return res;
}
