/**
 * 显示图片
 */
export function privateImage(path) {
  if(path.startsWith("/file")) {
     return import.meta.env.VITE_APP_BASE_API + path;
  }
  return path;
}
