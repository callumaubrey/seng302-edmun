// routes.js

import Home from './pages/Login/App';
import Register from './pages/Registration/App';
import Login from './pages/Login/App';
import Profile from './pages/Profile/App';
import EditProfile from './pages/EditProfile/User';
import Admin from './pages/AdminProfile/App';
import CreateActivity from './pages/CreateActivity/App';
import Activity from './pages/ViewActivity/App';

const routes = [
    { path: '/', component: Home },
    { path: '/register', component: Register },
    { path: '/login', component: Login },
    { path: '/profile/:id', component: Profile},
    { path: '/profile/edit/:id', component: EditProfile},
    { path: '/admin', component: Admin},
    { path: '/activity/new', component: CreateActivity },
    { path: '/profiles/:profileId/activities/:activityId', component: Activity}
];

export default routes;