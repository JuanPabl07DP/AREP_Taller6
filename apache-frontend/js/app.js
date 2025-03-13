// Configuración del API Backend
const API_URL = '/api';

// Función para verificar el estado de autenticación
function checkAuth() {
    const token = localStorage.getItem('jwt_token');
    const userEmail = localStorage.getItem('user_email');

    // Si estamos en la página de usuario y no hay token, redirigir al login
    if (window.location.pathname.includes('user.html') && (!token || !userEmail)) {
        window.location.href = 'login.html';
        return;
    }

    // Si estamos en login o register y hay token, redirigir al perfil
    if ((window.location.pathname.includes('login.html') || window.location.pathname.includes('register.html')) && token && userEmail) {
        window.location.href = 'user.html';
        return;
    }

    // Si estamos en la página de usuario, cargar los datos
    if (window.location.pathname.includes('user.html') && token && userEmail) {
        document.getElementById('user-email').textContent = userEmail;
        loadSecureData();
    }
}

// Función para realizar solicitudes al API
async function apiRequest(endpoint, method = 'GET', data = null) {
    const headers = {
        'Content-Type': 'application/json'
    };

    // Añadir token si existe
    const token = localStorage.getItem('jwt_token');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        method,
        headers,
    };

    if (data && (method === 'POST' || method === 'PUT')) {
        config.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(`${API_URL}${endpoint}`, config);

        // Si la respuesta no es exitosa
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Error en la solicitud');
        }

        return await response.json();
    } catch (error) {
        console.error('API Request Error:', error);
        throw error;
    }
}

// Cargar datos seguros para la página de usuario
async function loadSecureData() {
    const dataContainer = document.getElementById('secure-data-container');

    try {
        const userData = await apiRequest('/users');

        if (userData && userData.length) {
            let html = '<div class="user-table">';
            html += '<h4>Usuarios registrados (ejemplo de datos protegidos)</h4>';
            html += '<table><thead><tr><th>Email</th></tr></thead><tbody>';

            userData.forEach(user => {
                html += `<tr><td>${user.mail}</td></tr>`;
            });

            html += '</tbody></table></div>';
            dataContainer.innerHTML = html;
        } else {
            dataContainer.innerHTML = '<p>No hay datos disponibles.</p>';
        }
    } catch (error) {
        dataContainer.innerHTML = `<p class="error-message">Error al cargar datos: ${error.message}</p>`;
    }
}

// Manejador para el formulario de login
function setupLoginForm() {
    const loginForm = document.getElementById('login-form');
    if (!loginForm) return;

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const errorMessage = document.getElementById('error-message');

        try {
            const response = await apiRequest('/users/login', 'POST', {
                mail: email,
                password: password
            });

            // Guardar el token y email
            localStorage.setItem('jwt_token', response.token);
            localStorage.setItem('user_email', response.email);

            // Redirigir a la página de usuario
            window.location.href = 'user.html';
        } catch (error) {
            errorMessage.textContent = 'Error de autenticación: ' + (error.message || 'Credenciales inválidas');
        }
    });
}

// Manejador para el formulario de registro
function setupRegisterForm() {
    const registerForm = document.getElementById('register-form');
    if (!registerForm) return;

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirm-password').value;
        const errorMessage = document.getElementById('error-message');

        // Verificar que las contraseñas coinciden
        if (password !== confirmPassword) {
            errorMessage.textContent = 'Las contraseñas no coinciden';
            return;
        }

        try {
            const response = await apiRequest('/users/register', 'POST', {
                mail: email,
                password: password
            });

            // Guardar el token y email
            localStorage.setItem('jwt_token', response.token);
            localStorage.setItem('user_email', response.email);

            // Redirigir a la página de usuario
            window.location.href = 'user.html';
        } catch (error) {
            errorMessage.textContent = 'Error de registro: ' + (error.message || 'No se pudo crear la cuenta');
        }
    });
}

// Manejador para el enlace de logout
function setupLogout() {
    const logoutLink = document.getElementById('logout-link');
    if (!logoutLink) return;

    logoutLink.addEventListener('click', async (e) => {
        e.preventDefault();

        try {
            await apiRequest('/users/logout', 'POST');
        } catch (error) {
            console.error('Error en logout:', error);
        } finally {
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('user_email');

            // Redirigir a la página de inicio
            window.location.href = 'index.html';
        }
    });
}

// Inicializar la aplicación
document.addEventListener('DOMContentLoaded', function() {
    // Verificar autenticación
    checkAuth();

    // Configurar manejadores de eventos
    setupLoginForm();
    setupRegisterForm();
    setupLogout();
});