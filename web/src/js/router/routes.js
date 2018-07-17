import VueRouter from 'vue-router'
import Register from '../components/Register'
import Login from '../components/Login'
import Momos from '../components/Momos'
import MomoDetail from '../components/MomoDetail'
import CreateMomo from '../components/CreateMomo'

const routes = [
    {
        path: '/',
        component: Login,
        name: 'login'
    },
    {
        path: '/register',
        component: Register,
        name: 'register'
    },
    {
        path: '/momos',
        component: Momos,
        name: 'momos'
    },
    {
        path: '/momos/:id',
        component: MomoDetail,
        name: 'detail'
    },
    {
        path: '/momos/create',
        component: CreateMomo,
        name: 'create'
    }
]

const router = new VueRouter({
    routes
});

export default router