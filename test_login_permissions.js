// 测试登录和权限验证
async function testLoginAndPermissions() {
    console.log('=== 登录和权限测试 ===\n');
    
    const BASE_URL = 'http://localhost:8989';
    
    // 1. 测试普通用户登录
    console.log('1. 测试普通用户登录:');
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
        console.log('登录结果:', loginResult);
        
        if (loginResult.code === 200 && loginResult.data?.token) {
            const token = loginResult.data.token;
            console.log('✅ 普通用户登录成功\n');
            
            // 2. 测试访问需要认证的接口
            console.log('2. 测试访问需要认证的接口:');
            try {
                const authResponse = await fetch(`${BASE_URL}/api/auth/info`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                
                const authResult = await authResponse.json();
                console.log('认证接口结果:', authResult);
                console.log(authResult.code === 200 ? '✅ 认证接口访问成功\n' : '❌ 认证接口访问失败\n');
            } catch (error) {
                console.log('❌ 认证接口访问异常:', error.message, '\n');
            }
            
            // 3. 测试访问管理员接口（应该被拒绝）
            console.log('3. 测试访问管理员接口:');
            try {
                const adminResponse = await fetch(`${BASE_URL}/api/admin/users`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                
                console.log('管理员接口状态码:', adminResponse.status);
                if (adminResponse.status === 403) {
                    console.log('✅ 权限控制正常：普通用户无法访问管理员接口\n');
                } else {
                    console.log('❌ 权限控制异常\n');
                }
            } catch (error) {
                console.log('管理员接口访问异常:', error.message, '\n');
            }
        }
    } catch (error) {
        console.log('❌ 登录测试失败:', error.message, '\n');
    }
    
    // 4. 测试管理员登录
    console.log('4. 测试管理员登录:');
    try {
        const adminLoginData = {
            username: 'admin',
            password: 'admin123'
        };
        
        const adminLoginResponse = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(adminLoginData)
        });
        
        const adminLoginResult = await adminLoginResponse.json();
        console.log('管理员登录结果:', adminLoginResult);
        
        if (adminLoginResult.code === 200 && adminLoginResult.data?.token) {
            const adminToken = adminLoginResult.data.token;
            console.log('✅ 管理员登录成功\n');
            
            // 5. 测试管理员访问管理员接口
            console.log('5. 测试管理员访问管理员接口:');
            try {
                const adminUsersResponse = await fetch(`${BASE_URL}/api/admin/users`, {
                    headers: {
                        'Authorization': `Bearer ${adminToken}`
                    }
                });
                
                console.log('管理员用户接口状态码:', adminUsersResponse.status);
                if (adminUsersResponse.status === 200) {
                    console.log('✅ 管理员权限正常\n');
                } else if (adminUsersResponse.status === 403) {
                    console.log('❌ 管理员权限异常：无法访问管理员接口\n');
                }
            } catch (error) {
                console.log('管理员接口访问异常:', error.message, '\n');
            }
        }
    } catch (error) {
        console.log('❌ 管理员登录测试失败:', error.message, '\n');
    }
    
    console.log('=== 测试完成 ===');
}

// 运行测试
testLoginAndPermissions().catch(console.error);