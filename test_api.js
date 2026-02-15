// API测试脚本
const BASE_URL = 'http://localhost:8080';

async function testAPI() {
    console.log('=== 在线学习系统 API 测试 ===\n');
    
    // 1. 测试获取课程分类
    console.log('1. 测试获取课程分类:');
    try {
        const categoryResponse = await fetch(`${BASE_URL}/api/categories`);
        const categoryData = await categoryResponse.json();
        console.log('✅ 分类接口正常:', categoryData);
    } catch (error) {
        console.log('❌ 分类接口异常:', error.message);
    }
    
    // 2. 测试获取课程列表
    console.log('\n2. 测试获取课程列表:');
    try {
        const courseResponse = await fetch(`${BASE_URL}/api/courses?page=1&size=10`);
        const courseData = await courseResponse.json();
        console.log('✅ 课程列表接口正常:', courseData);
    } catch (error) {
        console.log('❌ 课程列表接口异常:', error.message);
    }
    
    // 3. 测试用户注册
    console.log('\n3. 测试用户注册:');
    try {
        const registerData = {
            username: 'testuser_' + Date.now(),
            password: 'test123',
            email: `test_${Date.now()}@example.com`,
            nickname: '测试用户'
        };
        
        const registerResponse = await fetch(`${BASE_URL}/api/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(registerData)
        });
        
        const registerResult = await registerResponse.json();
        console.log('✅ 注册接口正常:', registerResult);
    } catch (error) {
        console.log('❌ 注册接口异常:', error.message);
    }
    
    // 4. 测试用户登录
    console.log('\n4. 测试用户登录:');
    try {
        const loginData = {
            username: 'testuser',
            password: 'user123'
        };
        
        const loginResponse = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });
        
        const loginResult = await loginResponse.json();
        console.log('✅ 登录接口正常:', loginResult);
        
        // 如果登录成功，测试需要认证的接口
        if (loginResult.code === 200 && loginResult.data?.token) {
            console.log('\n5. 测试认证接口:');
            try {
                const authResponse = await fetch(`${BASE_URL}/api/auth/info`, {
                    headers: {
                        'Authorization': `Bearer ${loginResult.data.token}`
                    }
                });
                
                const authResult = await authResponse.json();
                console.log('✅ 认证接口正常:', authResult);
            } catch (error) {
                console.log('❌ 认证接口异常:', error.message);
            }
        }
    } catch (error) {
        console.log('❌ 登录接口异常:', error.message);
    }
    
    console.log('\n=== 测试完成 ===');
}

// 运行测试
testAPI();