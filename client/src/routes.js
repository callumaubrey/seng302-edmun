// routes.js

import Home from './pages/Login/App';
import Register from './pages/Registration/App';
import Login from './pages/Login/App';
import Profile from './pages/Profile/App';
import EditProfile from './pages/EditProfile/User'
import Admin from './pages/AdminProfile/App'

const routes = [
    { path: '/', component: Home },
    { path: '/register', component: Register },
    { path: '/login', component: Login },
    { path: '/profile/:id', component: Profile},
    { path: '/profile/edit/:id', component: EditProfile},
    { path: '/admin', component: Admin}
];

export default routes;