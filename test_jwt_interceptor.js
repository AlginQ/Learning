async function testJwtInterceptor() {
    console.log('=== JWT全局拦截器测试 ===\n');
    
    const BASE_URL = 'http://localhost:8989';
    
    // 测试1：验证登录接口可以正常访问（白名单）
    console.log('1. 测试登录接口白名单:');
    try {
        const loginData = {
            username: 'admin',
            password: 'admin123'
        };
        
        const response = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });
        
        const result = await response.json();
        console.log('登录接口状态码:', response.status);
        if (response.status === 200 && result.code === 200) {
            console.log('✅ 登录接口白名单配置正确');
            console.log('获得token:', result.data?.token?.substring(0, 20) + '...');
        } else {
            console.log('❌ 登录接口访问失败');
        }
    } catch (error) {
        console.log('❌ 登录接口测试异常:', error.message);
    }
    
    console.log('\n2. 测试无token访问受保护接口:');
    try {
        const response = await fetch(`${BASE_URL}/api/auth/info`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
                // 故意不带Authorization头
            }
        });
        
        console.log('无token访问状态码:', response.status);
        if (response.status === 401) {
            const result = await response.json();
            console.log('✅ 拦截器正确拦截无token请求:', result.msg);
        } else {
            console.log('❌ 拦截器未能正确拦截');
        }
    } catch (error) {
        console.log('❌ 无token测试异常:', error.message);
    }
    
    console.log('\n3. 测试无效token访问受保护接口:');
    try {
        const response = await fetch(`${BASE_URL}/api/auth/info`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer invalid-token-here'
            }
        });
        
        console.log('无效token访问状态码:', response.status);
        if (response.status === 401) {
            const result = await response.json();
            console.log('✅ 拦截器正确拦截无效token:', result.msg);
        } else {
            console.log('❌ 拦截器未能正确拦截无效token');
        }
    } catch (error) {
        console.log('❌ 无效token测试异常:', error.message);
    }
    
    console.log('\n4. 测试有效token访问受保护接口:');
    try {
        // 先获取有效token
        const loginResponse = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: 'admin',
                password: 'admin123'
            })
        });
        
        const loginResult = await loginResponse.json();
        if (loginResult.code === 200 && loginResult.data?.token) {
            const token = loginResult.data.token;
            
            const response = await fetch(`${BASE_URL}/api/auth/info`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            console.log('有效token访问状态码:', response.status);
            if (response.status === 200) {
                const result = await response.json();
                console.log('✅ 有效token认证通过，用户信息:', result.data?.username);
            } else {
                console.log('❌ 有效token认证失败');
            }
        }
    } catch (error) {
        console.log('❌ 有效token测试异常:', error.message);
    }
    
    console.log('\n=== 测试完成 ===');
}

testJwtInterceptor();