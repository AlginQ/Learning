async function testAdminAccessControl() {
    console.log('=== 管理员接口访问控制测试 ===\n');
    
    const BASE_URL = 'http://localhost:8989';
    
    // 测试1：普通用户登录获取token
    console.log('1. 普通用户登录获取token:');
    let userToken = '';
    try {
        const loginResponse = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: 'testuser',
                password: 'user123'
            })
        });
        
        const loginResult = await loginResponse.json();
        if (loginResult.code === 200 && loginResult.data?.token) {
            userToken = loginResult.data.token;
            console.log('✅ 普通用户登录成功，角色:', loginResult.data.role);
        }
    } catch (error) {
        console.log('❌ 普通用户登录失败:', error.message);
        return;
    }
    
    // 测试2：普通用户尝试访问管理员接口
    console.log('\n2. 普通用户访问管理员接口:');
    try {
        const response = await fetch(`${BASE_URL}/api/admin/users`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${userToken}`
            }
        });
        
        console.log('访问管理员接口状态码:', response.status);
        if (response.status === 403) {
            console.log('✅ 权限控制正常：普通用户无法访问管理员接口');
        } else {
            console.log('❌ 权限控制异常：普通用户意外访问成功');
        }
    } catch (error) {
        console.log('❌ 管理员接口访问测试异常:', error.message);
    }
    
    // 测试3：管理员用户登录获取token
    console.log('\n3. 管理员用户登录获取token:');
    let adminToken = '';
    try {
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
            adminToken = loginResult.data.token;
            console.log('✅ 管理员登录成功，角色:', loginResult.data.role);
        }
    } catch (error) {
        console.log('❌ 管理员登录失败:', error.message);
        return;
    }
    
    // 测试4：管理员用户访问管理员接口
    console.log('\n4. 管理员访问管理员接口:');
    try {
        const response = await fetch(`${BASE_URL}/api/admin/users`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${adminToken}`
            }
        });
        
        console.log('管理员访问接口状态码:', response.status);
        if (response.status === 200) {
            console.log('✅ 权限控制正常：管理员可以访问管理员接口');
        } else {
            console.log('❌ 权限控制异常：管理员访问被拒绝');
        }
    } catch (error) {
        console.log('❌ 管理员接口访问测试异常:', error.message);
    }
    
    console.log('\n=== 测试完成 ===');
}

testAdminAccessControl();