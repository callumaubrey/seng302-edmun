// routes.js

import Home from './pages/Login/App';
import Login from './pages/Login/App';
import Register from './pages/Registration/App';
import Profile from './pages/Profile/App';
import EditProfile from './pages/EditProfile/User';
import Admin from './pages/AdminProfile/App';
import AdminDashboard from './pages/AdminDashboard/App';
import CreateActivity from './pages/CreateActivity/App';
import Activity from './pages/ViewActivity/App';
import EditActivity from "./pages/EditActivity/EditActivity";
import ListActivity from './pages/ListActivity/App';
import SearchUsers from './pages/SearchUsers/App';
import SearchActivity from "./pages/SearchActivity/App";
import HashTag from './pages/HashTag/App';
import HomeFeed from './pages/HomeFeed/App';
import ForgotPassword from './pages/ForgotPassword/App';
import ResetPassword from './pages/ResetPassword/App';

const routes = [
    {path: '/', component: Home},
    {path: '/register', component: Register},
    {path: '/login', component: Login},
    {path: '/admin', component: Admin},
    {
        path: '/admin/dashboard',
        component: AdminDashboard,
        name: 'AdminDashboard'
    },
    {path: '/profiles/:id', component: Profile, name: 'Profile'},
    {path: '/profiles/edit/:id', component: EditProfile},
    {path: '/profiles/:id/activities/create', component: CreateActivity},
    {
        path: '/profiles/:id/activities/:activityId/edit',
        component: EditActivity
    },
    {path: '/profiles/:id/activities/:activityId', component: Activity},
    {path: '/profiles/:id/activities/', component: ListActivity},
    {path: '/activities/search', component: SearchActivity},
    {path: '/profiles', component: SearchUsers, name: 'Users'},
    {path: '/hashtag/:hashtag', component: HashTag},
    {path: '/home', component: HomeFeed},
    {path: '/forgot-password', component: ForgotPassword},
    {path: '/home', component: HomeFeed},
    {path: '/resetpassword/:token', component: ResetPassword}
];

export default routes;
