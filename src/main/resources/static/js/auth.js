const API_BASE = '/api';

function getToken() {
    return localStorage.getItem('rm_token');
}

function getSession() {
    const raw = localStorage.getItem('rm_session');
    return raw ? JSON.parse(raw) : null;
}

function saveSession(token, nombre, rol) {
    localStorage.setItem('rm_token', token);
    localStorage.setItem('rm_session', JSON.stringify({ nombre, rol }));
}

function clearSession() {
    localStorage.removeItem('rm_token');
    localStorage.removeItem('rm_session');
}

function guardRoute() {
    const token = getToken();
    if (!token) {
        window.location.replace('/login.html');
        return null;
    }
    return getSession();
}

function logout() {
    clearSession();
    window.location.replace('/login.html');
}

const loginForm = document.getElementById('loginForm');
if (loginForm) {
    if (getToken()) {
        window.location.replace('/dashboard.html');
    }

    loginForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        const id = document.getElementById('inputId').value.trim();
        const cargo = document.getElementById('selectCargo').value;
        const contrasena = document.getElementById('inputContrasena').value;
        const errorMsg = document.getElementById('errorMsg');
        const btnLogin = document.getElementById('btnLogin');

        if (!id || !cargo || !contrasena) {
            errorMsg.textContent = 'Todos los campos son obligatorios.';
            errorMsg.style.display = 'block';
            return;
        }

        errorMsg.style.display = 'none';
        btnLogin.disabled = true;
        btnLogin.textContent = 'Verificando...';

        try {
            const res = await fetch(API_BASE + '/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id, cargo, contrasena })
            });

            const body = await res.json();

            if (body.ok) {
                saveSession(body.data.token, body.data.nombre, body.data.rol);
                window.location.replace('/dashboard.html');
            } else {
                errorMsg.textContent = body.message || 'Credenciales incorrectas.';
                errorMsg.style.display = 'block';
            }
        } catch (err) {
            errorMsg.textContent = 'No se pudo conectar con el servidor.';
            errorMsg.style.display = 'block';
        } finally {
            btnLogin.disabled = false;
            btnLogin.textContent = 'Iniciar Sesion';
        }
    });
}
