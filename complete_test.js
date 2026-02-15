const http = require('http');

// 测试分类接口
function testCategories() {
    const options = {
        hostname: 'localhost',
        port: 8080,
        path: '/api/categories',
        method: 'GET'
    };

    const req = http.request(options, (res) => {
        let data = '';
        
        res.on('data', (chunk) => {
            data += chunk;
        });
        
        res.on('end', () => {
            console.log('=== 分类接口测试 ===');
            console.log('Status:', res.statusCode);
            console.log('Response:', data);
            console.log('');
        });
    });

    req.on('error', (error) => {
        console.error('分类接口错误:', error.message);
    });

    req.end();
}

// 测试课程列表接口
function testCourses() {
    const options = {
        hostname: 'localhost',
        port: 8080,
        path: '/api/courses?page=1&size=5',
        method: 'GET'
    };

    const req = http.request(options, (res) => {
        let data = '';
        
        res.on('data', (chunk) => {
            data += chunk;
        });
        
        res.on('end', () => {
            console.log('=== 课程列表接口测试 ===');
            console.log('Status:', res.statusCode);
            console.log('Response:', data);
            console.log('');
        });
    });

    req.on('error', (error) => {
        console.error('课程列表接口错误:', error.message);
    });

    req.end();
}

// 测试用户注册
function testRegister() {
    const postData = JSON.stringify({
        username: 'testuser_' + Date.now(),
        password: 'test123',
        email: `test_${Date.now()}@example.com`,
        nickname: '测试用户'
    });

    const options = {
        hostname: 'localhost',
        port: 8080,
        path: '/api/auth/register',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Content-Length': Buffer.byteLength(postData)
        }
    };

    const req = http.request(options, (res) => {
        let data = '';
        
        res.on('data', (chunk) => {
            data += chunk;
        });
        
        res.on('end', () => {
            console.log('=== 用户注册接口测试 ===');
            console.log('Status:', res.statusCode);
            console.log('Response:', data);
            console.log('');
        });
    });

    req.on('error', (error) => {
        console.error('用户注册接口错误:', error.message);
    });

    req.write(postData);
    req.end();
}

// 执行测试
console.log('=== 在线学习系统 API 测试 ===\n');
testCategories();
testCourses();
testRegister();