module.exports={
    devServer:{
        proxy:{
            ["/python"]:{
                target:'http://localhost:5000',
                changeOrigin:true,
                pathRewrite: {
                    ['^' + "/python"]: ''
                }
            }
        }

    }
}