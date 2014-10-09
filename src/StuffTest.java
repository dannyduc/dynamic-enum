import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StuffTest {

    public static void main(String[] args) throws Exception {
        Field[] declaredFields = Stuff.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if ("$VALUES".equals(declaredField.getName())) {
                declaredField.setAccessible(true);
                Stuff[] prevArray = (Stuff[]) declaredField.get(null);

                List<Stuff> values = new ArrayList<Stuff>(Arrays.asList(prevArray));

                Class<?>[] parameterTypes = new Class[2];
                parameterTypes[0] = String.class;
                parameterTypes[1] = int.class;

                Constructor<?> declaredConstructor = Stuff.class.getDeclaredConstructor(parameterTypes);
                declaredConstructor.setAccessible(true);
//                declaredConstructor.newInstance("PIG", values.size());
                Object[] params = new Object[] { "PIG", values.size() };
                ConstructorAccessor constructorAccessor = ReflectionFactory.getReflectionFactory().newConstructorAccessor(declaredConstructor);
                Stuff pig = Stuff.class.cast(constructorAccessor.newInstance(params));

                values.add(pig);

                declaredField.setAccessible(true);

                Field modifiersField = declaredField.getClass().getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                int modifiers = modifiersField.getInt(declaredField);
                modifiers &= ~Modifier.FINAL;
                modifiersField.setInt(declaredField, modifiers);

                FieldAccessor fa = ReflectionFactory.getReflectionFactory().newFieldAccessor(declaredField, false);
                fa.set(null, values.toArray((Stuff[]) Array.newInstance(Stuff.class, 0)));

                for (Field field : Stuff.class.getDeclaredFields()) {
                    if (field.getName().contains("enumConstantDirectory")
                            || field.getName().contains("enumConstants")) {
                        AccessibleObject.setAccessible(new Field[] { field }, true);
//                        setFailsafeFieldValue(field, enumClass, null);
                        break;
                    }
                }
            }
        }

        System.out.println(Arrays.deepToString(Stuff.values()));
    }
}
