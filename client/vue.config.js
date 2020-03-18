module.exports = {
    runtimeCompiler: true,
    publicPath: process.env.VUE_APP_BASE_URL,
    pages: {
        'index': {
            entry: './src/pages/Login/main.js',
            template: 'public/index.html',
            title: 'Login',
            chunks: ['chunk-vendors', 'chunk-common', 'index']
        },
        'registration': {
            entry: './src/pages/Registration/main.js',
            template: 'public/index.html',
            title: 'Registration',
            chunks: ['chunk-vendors', 'chunk-common', 'registration']
        },
        'profile': {
            entry: './src/pages/Profile/main.js',
            template: 'public/index.html',
            title: 'Profile',
            chunks: ['chunk-vendors', 'chunk-common', 'profile']
        },
        'editProfile': {
            entry: './src/pages/EditProfile/main.js',
            template: 'public/index.html',
            title: 'EditProfile',
            chunks: ['chunk-vendors', 'chunk-common', 'editProfile']
        },
        'adminProfile': {
            entry: './src/pages/AdminProfile/main.js',
            template: 'public/index.html',
            title: 'AdminProfile',
            chunks: ['chunk-vendors', 'chunk-common', 'adminProfile']
        }
    }
};
