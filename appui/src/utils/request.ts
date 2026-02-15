import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

class HttpRequest {
  private instance: AxiosInstance

  constructor() {
    this.instance = axios.create({
      baseURL: '/api',
      timeout: 10000
    })

    this.setupInterceptors()
  }

  private setupInterceptors() {
    // 请求拦截器
    this.instance.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // 响应拦截器
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => {
        const { code, msg } = response.data
        if (code !== 200) {
          ElMessage.error(msg || '请求失败')
          return Promise.reject(new Error(msg || '请求失败'))
        }
        return response.data
      },
      (error) => {
        if (error.response) {
          const { status, data } = error.response
          switch (status) {
            case 401:
              ElMessage.error('登录已过期，请重新登录')
              localStorage.removeItem('token')
              window.location.href = '/login'
              break
            case 403:
              ElMessage.error('权限不足')
              break
            case 404:
              ElMessage.error('请求的资源不存在')
              break
            case 500:
              ElMessage.error('服务器内部错误')
              break
            default:
              ElMessage.error(data?.msg || '请求失败')
          }
        } else {
          ElMessage.error('网络错误')
        }
        return Promise.reject(error)
      }
    )
  }

  // GET 请求
  get<T>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.get(url, { params, ...config })
  }

  // POST 请求
  post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.post(url, data, config)
  }

  // PUT 请求
  put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.put(url, data, config)
  }

  // DELETE 请求
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.delete(url, config)
  }
}

export default new HttpRequest()