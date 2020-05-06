// routes.js

import Home from './pages/Login/App';
import Login from './pages/Login/App';
import Register from './pages/Registration/App';
import Profile from './pages/Profile/App';
import EditProfile from './pages/EditProfile/User';
import Admin from './pages/AdminProfile/App';
import CreateActivity from './pages/CreateActivity/App';
import Activity from './pages/ViewActivity/App';
import EditActivity from "./pages/EditActivity/EditActivity";
import ListActivity from './pages/ListActivity/App';

const routes = [
    {path: '/', component: Home},
    {path: '/register', component: Register},
    {path: '/login', component: Login},
    {path: '/profiles/:id', component: Profile},
    {path: '/profiles/edit/:id', component: EditProfile},
    {path: '/admin', component: Admin},
    {path: '/profiles/:id/activities/create', component: CreateActivity},
    {path: '/profiles/:id/activities/:activityId/edit', component: EditActivity},
    {path: '/profiles/:id/activities/:activityId', component: Activity},
    {path: '/profiles/:id/activities/', component: ListActivity}
];

export default routes;