package com.example.haunted;

import com.example.haunted.engine.CombatEngine;
import com.example.haunted.model.*;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;
import com.example.haunted.rules.TrapResolver;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import org.junit.jupiter.api.Test;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Deliberately implementation-sensitive checks, separate from behavior tests. */
public class ImplementationContractTest {
    @Test
    void inventoryReturnsBackingListsAddResult() throws Exception {
        Inventory inventory = new Inventory(2);
        Item key = new Key("Key", "Test item.");
        @SuppressWarnings("unchecked")
        List<Item> backingList = mock(List.class);
        when(backingList.add(key)).thenReturn(false);
        Field items = Inventory.class.getDeclaredField("items");
        items.setAccessible(true);
        items.set(inventory, backingList);

        assertFalse(inventory.addItem(key));

        verify(backingList).add(key);
    }

    @Test
    void combatInitializesDamageToPlayerWithZero() throws Exception {
        DamageCalculator calculator = mock(DamageCalculator.class);
        Player player = new Player("Student", 50, 7, 2, new Inventory(8));
        Monster monster = new Monster("Phantom", 20, 8, 2, List.of());
        CombatEngine engine = new CombatEngine(calculator, new QuestTracker());
        when(calculator.calculatePlayerDamage(player, monster)).thenReturn(10);
        when(calculator.calculateMonsterDamage(monster, player)).thenReturn(4);
        assertEquals(4, engine.attack(player, new Quest("Escape", "Test quest."), monster).getDamageToPlayer());

        int[] initialValues = new int[32];
        boolean[] seenStore = new boolean[32];
        int[] damageSlot = {-1};
        new ClassReader(runtimeBytes(CombatEngine.class)).accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                             String signature, String[] exceptions) {
                if (!name.equals("attack")) {
                    return null;
                }
                return new MethodVisitor(Opcodes.ASM9) {
                    private int previousOpcode = -1;

                    @Override
                    public void visitInsn(int opcode) {
                        previousOpcode = opcode;
                    }

                    @Override
                    public void visitVarInsn(int opcode, int slot) {
                        if (opcode == Opcodes.ISTORE && slot < seenStore.length && !seenStore[slot]) {
                            seenStore[slot] = true;
                            initialValues[slot] = previousOpcode;
                        }
                        previousOpcode = -1;
                    }

                    @Override
                    public void visitLocalVariable(String variable, String descriptor, String signature,
                                                   net.bytebuddy.jar.asm.Label start,
                                                   net.bytebuddy.jar.asm.Label end, int slot) {
                        if (variable.equals("damageToPlayer")) {
                            damageSlot[0] = slot;
                        }
                    }
                };
            }
        }, 0);
        assertTrue(damageSlot[0] >= 0, "Requires local-variable debug information");
        assertTrue(seenStore[damageSlot[0]]);
        assertEquals(Opcodes.ICONST_0, initialValues[damageSlot[0]], "Initial damageToPlayer instruction");
    }

    @Test
    void trapMessageAllocatesExactlyTwoFormattingArguments() throws Exception {
        Player player = new Player("Student", 50, 7, 2, new Inventory(8));
        Trap trap = new Trap("Steam", TrapType.STEAM, 8, true, false);
        assertEquals("Trap 'Steam' triggered for 8 damage.",
                new TrapResolver().resolveTrap(player, trap).getMessage());

        int[] arraySizeOpcode = {-1};
        new ClassReader(runtimeBytes(TrapResolver.class)).accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                             String signature, String[] exceptions) {
                if (!name.equals("resolveTrap")) {
                    return null;
                }
                return new MethodVisitor(Opcodes.ASM9) {
                    private int previousOpcode = -1;

                    @Override
                    public void visitInsn(int opcode) {
                        previousOpcode = opcode;
                    }

                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        if (opcode == Opcodes.ANEWARRAY && type.equals("java/lang/Object")) {
                            arraySizeOpcode[0] = previousOpcode;
                        }
                    }
                };
            }
        }, 0);
        assertEquals(Opcodes.ICONST_2, arraySizeOpcode[0], "Formatting argument array size instruction");
    }

    private byte[] runtimeBytes(Class<?> type) throws Exception {
        Instrumentation instrumentation = ByteBuddyAgent.install();
        byte[][] captured = {null};
        ClassFileTransformer transformer = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String name, Class<?> redefined,
                                    ProtectionDomain domain, byte[] bytes) {
                if (redefined == type) {
                    captured[0] = bytes.clone();
                }
                return null;
            }
        };
        instrumentation.addTransformer(transformer, true);
        try {
            instrumentation.retransformClasses(type);
        } finally {
            instrumentation.removeTransformer(transformer);
        }
        assertNotNull(captured[0], "Could not capture loaded class definition");
        return captured[0];
    }
}
