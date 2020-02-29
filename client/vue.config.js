module.exports = {
    runtimeCompiler: true,
    publicPath: process.env.VUE_APP_BASE_URL,
    pages: {
        'index': {
            entry: './src/main.js',
            template: 'public/index.html',
            title: 'Home',
            chunks: ['chunk-vendors', 'chunk-common', 'index']
        },
        'login': {
            entry: './src/pages/Login/main.js',
            template: 'public/index.html',
            title: 'Login',
            chunks: ['chunk-vendors', 'chunk-common', 'login']
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
        }
    }
};