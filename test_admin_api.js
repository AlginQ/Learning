const http = require('http');

// 测试管理员获取用户列表接口
function testAdminGetUsers() {
    const options = {
        hostname: 'localhost',
        port: 8989,
        path: '/api/admin/users',
        method: 'GET',
        headers: {
            'Authorization': 'Bearer your-admin-token-here'
        }
    };

    const req = http.request(options, (res) => {
        console.log(`获取用户列表 - 状态码: ${res.statusCode}`);
        
        res.setEncoding('utf8');
        let rawData = '';
        res.on('data', (chunk) => { rawData += chunk; });
        res.on('end', () => {
            try {
                const parsedData = JSON.parse(rawData);
                console.log('响应数据:', JSON.stringify(parsedData, null, 2));
            } catch (e) {
                console.error('解析响应数据出错:', e.message);
                console.log('原始响应:', rawData);
            }
        });
    });

    req.on('error', (e) => {
        console.error(`请求遇到问题: ${e.message}`);
    });

    req.end();
}

// 测试管理员删除用户接口
function testAdminDeleteUser() {
    const options = {
        hostname: 'localhost',
        port: 8989,
        path: '/api/admin/users/1',
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer your-admin-token-here'
        }
    };

    const req = http.request(options, (res) => {
        console.log(`删除用户 - 状态码: ${res.statusCode}`);
        
        res.setEncoding('utf8');
        let rawData = '';
        res.on('data', (chunk) => { rawData += chunk; });
        res.on('end', () => {
            try {
                const parsedData = JSON.parse(rawData);
                console.log('响应数据:', JSON.stringify(parsedData, null, 2));
            } catch (e) {
                console.error('解析响应数据出错:', e.message);
                console.log('原始响应:', rawData);
            }
        });
    });

    req.on('error', (e) => {
        console.error(`请求遇到问题: ${e.message}`);
    });

    req.end();
}

// 执行测试
console.log('=== 管理员接口测试 ===\n');
testAdminGetUsers();
setTimeout(testAdminDeleteUser, 1000);