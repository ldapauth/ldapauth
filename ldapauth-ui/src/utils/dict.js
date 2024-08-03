import useDictStore from '@/store/modules/dict'
import distData from '@/utils/distData'
/**
 * 获取字典数据
 */
export function useDict(...args) {
  const res = ref({});
  return (() => {
    args.forEach((dictType, index) => {
      res.value[dictType] = [];
      const dicts = useDictStore().getDict(dictType);
      if (dicts) {
        res.value[dictType] = dicts;
      } else {
        res.value[dictType] = distData[dictType];
        useDictStore().setDict(dictType, res.value[dictType]);
      }
    })
    return toRefs(res.value);
  })()
}
