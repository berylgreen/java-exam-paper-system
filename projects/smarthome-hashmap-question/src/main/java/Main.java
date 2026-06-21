import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        HashMap<String, Device> deviceMap = new HashMap<>();

        addDevice(deviceMap, "101", "智能灯");
        addDevice(deviceMap, "102", "智能空调");
        addDevice(deviceMap, "103", "智能门锁");
        addDevice(deviceMap, "104", "智能摄像头");

        System.out.println("添加4个设备后：");
        printAll(deviceMap);

        System.out.println("查询102对应的设备信息：");
        System.out.println(formatDevice(getDeviceById(deviceMap, "102")));

        System.out.println("删除102对应的设备信息：");
        System.out.println(formatDevice(removeDeviceById(deviceMap, "102")));

        System.out.println("删除后再次查询102：");
        System.out.println(formatDevice(getDeviceById(deviceMap, "102")));
    }

    private static void addDevice(HashMap<String, Device> deviceMap, String id, String name) {
        deviceMap.put(id, createDevice(id, name));
    }

    private static Device getDeviceById(HashMap<String, Device> deviceMap, String id) {
        return deviceMap.get(id);
    }

    private static Device removeDeviceById(HashMap<String, Device> deviceMap, String id) {
        return deviceMap.remove(id);
    }

    private static void printAll(HashMap<String, Device> deviceMap) {
        deviceMap.forEach((id, device) -> System.out.println(id + " -> " + formatDevice(device)));
    }

    private static Device createDevice(String id, String name) {
        try {
            Device device = instantiateDevice(id, name);
            setFieldIfPresent(device, id, "id", "deviceId");
            setFieldIfPresent(device, name, "name", "deviceName");
            return device;
        } catch (Exception e) {
            throw new RuntimeException("创建Device对象失败", e);
        }
    }

    private static Device instantiateDevice(String id, String name) throws Exception {
        try {
            Constructor<Device> constructor = Device.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException ignored) {
            // 继续尝试其他构造方法
        }

        for (Constructor<?> constructor : Device.class.getDeclaredConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                args[i] = defaultValue(parameterTypes[i], id, name, i);
            }

            try {
                constructor.setAccessible(true);
                return (Device) constructor.newInstance(args);
            } catch (Exception ignored) {
                // 继续尝试下一个构造方法
            }
        }

        throw new IllegalStateException("无法通过构造方法创建Device对象");
    }

    private static Object defaultValue(Class<?> type, String id, String name, int index) {
        if (type == String.class) {
            return index == 0 ? id : name;
        }
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == char.class) {
            return '\0';
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == float.class) {
            return 0F;
        }
        if (type == double.class) {
            return 0D;
        }
        return null;
    }

    private static void setFieldIfPresent(Object target, Object value, String... fieldNames) {
        if (target == null || fieldNames == null) {
            return;
        }
        for (String fieldName : fieldNames) {
            if (fieldName == null || fieldName.isEmpty()) {
                continue;
            }
            Field field = findField(target.getClass(), fieldName);
            if (field != null) {
                try {
                    field.setAccessible(true);
                    field.set(target, value);
                    return;
                } catch (Exception ignored) {
                    // 继续尝试其他字段名
                }
            }
        }
    }

    private static String getFieldValue(Object target, String... fieldNames) {
        if (target == null || fieldNames == null) {
            return null;
        }
        for (String fieldName : fieldNames) {
            if (fieldName == null || fieldName.isEmpty()) {
                continue;
            }
            Field field = findField(target.getClass(), fieldName);
            if (field != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(target);
                    return value == null ? null : String.valueOf(value);
                } catch (Exception ignored) {
                    // 继续尝试其他字段名
                }
            }
        }
        return null;
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private static String formatDevice(Device device) {
        if (device == null) {
            return "null";
        }
        String id = getFieldValue(device, "id", "deviceId");
        String name = getFieldValue(device, "name", "deviceName");
        if (id == null && name == null) {
            return String.valueOf(device);
        }
        return "Device{id='" + id + "', name='" + name + "'}";
    }
}
