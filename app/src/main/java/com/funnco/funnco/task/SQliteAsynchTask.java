package com.funnco.funnco.task;

import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

/**
 * Created by user on 2015/7/8.
 */
public class SQliteAsynchTask<T> {

    /**
     * 根据指定id查找记录
     * @param dbUtils 数据库操作的类
     * @param c 表名
     * @param id 依据id 为空则查找第一条记录
     * @return 返回查找记录的对象
     */
    public static Object selectT(DbUtils dbUtils, Class c, String id) {
        try {
            if (dbUtils != null && c != null && dbUtils.tableIsExist(c)) {
                if (!TextUtils.isNull(id)) {
                    return dbUtils.findById(c, id);
                } else {
                    return dbUtils.findFirst(c);
                }
            }
            }catch(DbException e){
                e.printStackTrace();
            }
            return null;
    }

    public static List selectTall(DbUtils dbUtils,Class c,String[] columns,String[] cars, String[] values){
        try {
            if (dbUtils != null && c != null && dbUtils.tableIsExist(c)){
                Selector selector = Selector.from(c);
                if (columns != null && cars != null && values != null) {
                    for (int i = 0; i < columns.length; i++) {
                        selector.where(columns[i], cars[i], values[i]);
                    }
                }
                return dbUtils.findAll(selector);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 保存或更新数据
     * @param dbUtils 数据库操作的工具类
     * @param list 需要保存的对象集合
     * @return 是否保存成功
     */
    public synchronized static boolean saveOrUpdate(DbUtils dbUtils,List<?> list){
        LogUtils.e("----","进行数据保存。。。");
        if (dbUtils != null && list!=null){
            try {
                LogUtils.e("----","保存数据集合大小："+list.size());
                dbUtils.saveOrUpdateAll(list);
            } catch (DbException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 根据Id 更新整个对象
     * @param dbUtils
     * @param cls
     * @param id
     * @param t
     * @return
     */
    public synchronized boolean updateTbyId(DbUtils dbUtils,Class cls,String id,T t){
        try {
            if (dbUtils != null && !TextUtils.isNull(id) && dbUtils.tableIsExist(cls)){
                T t2 = (T) dbUtils.findById(cls,id);
                if (t2 != null){
                    dbUtils.saveOrUpdate(t);
                }
            }else{
                return false;
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据Id 更新置顶的列数据
     * @param dbUtils
     * @param cls
     * @param id
     * @param colums
     * @param t
     * @return
     */
    public synchronized boolean updateTbyId(DbUtils dbUtils,Class cls,String id,String[] colums,T t){
        try {
            if (dbUtils != null && !TextUtils.isNull(id) && dbUtils.tableIsExist(cls) && t != null){
               if (dbUtils.findById(cls,id) !=null){
                    dbUtils.update(t,colums);
               }
            }else{
                return false;
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean saveOrUpdate(DbUtils dbUtils, T t){
        if (dbUtils != null && t != null){
            try {
                dbUtils.saveOrUpdate(t);
            } catch (DbException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 移除表中所有数据
     * @param dbUtils 数据库操作的工具类
     * @param t 需要删除的表名
     * @return 是否删除成功
     */
    public synchronized static boolean deleteAll(DbUtils dbUtils,Class t){
        try {
            if (dbUtils == null || t == null || !dbUtils.tableIsExist(t)){
                return false;
            }
            dbUtils.deleteAll(t);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 指定表内删除指定id的记录
     * @param dbUtils 数据库操作的类
     * @param c 表名（类名）
     * @param id 删除记录的id
     * @return 删除是否成功
     */
    public synchronized static boolean deleteById(DbUtils dbUtils,Class c,String id){
        if (dbUtils != null && c != null && id != null){
            try {
                if (dbUtils.tableIsExist(c)) {
                    dbUtils.deleteById(c, id);
                }else{
                    return false;
                }
            } catch (DbException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 根据指定条件进行删除
     * @param dbUtils 数据库操作的类
     * @param c 表名
     * @param columnName 类名
     * @param op 符号
     * @param value 满足的条件的值
     * @return 是否删除成功
     */
    public synchronized static boolean deleteByBuilder(DbUtils dbUtils,Class c,String columnName,String op,String value){
        if (dbUtils != null && c != null && columnName != null && op != null && value != null) {
            WhereBuilder builder = WhereBuilder.b(columnName, op, value);
            try {
                if (dbUtils.tableIsExist(c)) {
                    dbUtils.delete(c, builder);
                }
                return true;
            } catch (DbException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 删除数据表
     * @param dbUtils 数据库操作的工具类
     * @param c 要删除的表名
     * @return 是否删除成功
     */
    public synchronized static boolean dropTable(DbUtils dbUtils,Class c){
        if (dbUtils != null && c != null){
            try {
                if (dbUtils.tableIsExist(c)) {
                    dbUtils.dropTable(c);
                }else{
                    return false;
                }
            } catch (DbException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

}
