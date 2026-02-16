async function testLoginFix() {
    console.log('=== 登录接口权限修复验证测试 ===\n');
    
    const BASE_URL = 'http://localhost:8989';
    
    // 测试1：未登录状态下直接访问登录接口
    console.log('1. 测试未登录状态下访问登录接口:');
    try {
        const loginData = {
            username: 'admin',
            password: 'admin123'
        };
        
        const response = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
                // 注意：这里故意不带Authorization头，模拟未登录状态
            },
            body: JSON.stringify(loginData)
        });
        
        const result = await response.json();
        console.log('响应状态码:', response.status);
        console.log('响应结果:', result);
        
        if (response.status === 200 && result.code === 200) {
            console.log('✅ 修复成功：未登录用户可以正常访问登录接口\n');
        } else {
            console.log('❌ 仍有问题：登录接口仍被权限拦截\n');
        }
    } catch (error) {
        console.log('❌ 测试失败:', error.message, '\n');
    }
    
    // 测试2：验证登录后token的有效性
    console.log('2. 测试登录后获取的token是否有效:');
    try {
        const loginData = {
            username: 'admin',
            password: 'admin123'
        };
        
        const loginResponse = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });
        
        const loginResult = await loginResponse.json();
        
        if (loginResult.code === 200 && loginResult.data?.token) {
            const token = loginResult.data.token;
            console.log('登录成功，获得token');
            
            // 使用token访问需要认证的接口
            const authResponse = await fetch(`${BASE_URL}/api/auth/info`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            console.log('认证接口状态码:', authResponse.status);
            if (authResponse.status === 200) {
                console.log('✅ Token验证正常\n');
            } else {
                console.log('❌ Token验证失败\n');
            }
        }
    } catch (error) {
        console.log('❌ Token测试失败:', error.message, '\n');
    }
    
    console.log('=== 测试完成 ===');
}

testLoginFix();