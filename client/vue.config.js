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
        'registration': {
            entry: './src/pages/Registration/main.js',
            template: 'public/index.html',
            title: 'Registration',
            chunks: ['chunk-vendors', 'chunk-common', 'registration']
        }
    }
}