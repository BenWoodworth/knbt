package net.benwoodworth.knbt.internal

import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import net.benwoodworth.knbt.internal.NbtPath.NameNode
import net.benwoodworth.knbt.internal.NbtPath.RootNode
import net.benwoodworth.knbt.internal.NbtReader.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.test.fix
import net.benwoodworth.knbt.test.mocks.VerifyingMockFactory
import net.benwoodworth.knbt.test.mocks.VerifyingNbtReaderMock
import kotlin.test.Test

class PathTrackingNbtReaderTest {
    private fun withEachPrimitiveType(test: NbtReaderPrimitiveCalls<*>.() -> Unit) {
        val primitiveTypes = listOf(
            NbtReaderPrimitiveCalls.ByteCalls,
            NbtReaderPrimitiveCalls.ShortCalls,
            NbtReaderPrimitiveCalls.IntCalls,
            NbtReaderPrimitiveCalls.LongCalls,
            NbtReaderPrimitiveCalls.FloatCalls,
            NbtReaderPrimitiveCalls.DoubleCalls,
            NbtReaderPrimitiveCalls.StringCalls,
        )

        primitiveTypes.forAll { it.test() }
    }

    private fun withEachStructureType(size: Int, test: NbtReaderStructureCalls<*, *>.() -> Unit) {
        val structureTypes = listOf(
            NbtReaderStructureCalls.Compound,
            NbtReaderStructureCalls.ListCalls(size),
            NbtReaderStructureCalls.ByteArrayCalls(size),
            NbtReaderStructureCalls.IntArrayCalls(size),
            NbtReaderStructureCalls.LongArrayCalls(size),
        )

        structureTypes.forAll { it.test() }
    }

    @Test
    fun should_initialize_with_an_empty_path() {
        VerifyingNbtReaderMock
            .create {
                // delegate should not be called at all
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)

                reader.getPath() shouldBe NbtPath()
            }
    }

    @Test
    fun getting_the_path_should_return_an_unchanging_path() {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(TAG_Compound)
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)
                val initialPath = reader.getPath()

                // Changes the NbtReader's path
                reader.beginRootTag()

                initialPath shouldBe NbtPath()
            }
    }

    @Test
    fun beginning_a_root_tag_should_add_root_to_the_path() {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(TAG_Int)
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)

                reader.beginRootTag() shouldBe RootTagInfo(TAG_Int)
                reader.getPath() shouldBe NbtPath(RootNode(TAG_Int))
            }
    }

    @Test
    fun reading_a_primitive_should_not_change_the_path() = withEachPrimitiveType {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(primitiveType)
                readPrimitive() returns primitiveReadResult
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)
                reader.beginRootTag()

                val parentPath = reader.getPath()
                reader.readPrimitive() shouldBe primitiveReadResult
                reader.getPath() shouldBe parentPath
            }
    }

    @Test
    fun beginning_a_structure_should_leave_path_unchanged() = withEachStructureType(1) {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(structureType)
                beginStructure()
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)
                reader.beginRootTag()

                val parentPath = reader.getPath()
                reader.beginStructure()

                reader.getPath() shouldBe parentPath
            }
    }

    @Test
    fun beginning_the_first_structure_entry_should_add_to_the_parent_path() = withEachStructureType(1) {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(structureType)
                beginStructure()
                beginStructureEntry() returns firstEntryInfo
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)
                reader.beginRootTag()
                reader.beginStructure()

                val parentPath = reader.getPath()
                reader.beginStructureEntry() shouldBe firstEntryInfo
                reader.getPath() shouldBe (parentPath + getEntryPathNode(0))
            }
    }

    @Test
    fun beginning_the_second_structure_entry_should_replace_the_child_path() = withEachStructureType(2) {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(structureType)
                beginStructure()
                beginStructureEntry() returns firstEntryInfo
                beginStructureEntry() returns secondEntryInfo
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)
                reader.beginRootTag()
                reader.beginStructure()

                val parentPath = reader.getPath()
                reader.beginStructureEntry()
                reader.beginStructureEntry() shouldBe secondEntryInfo
                reader.getPath() shouldBe (parentPath + getEntryPathNode(1))
            }
    }

    @Test
    fun beginning_an_empty_structure_end_entry_should_leave_path_as_parent_path() = withEachStructureType(0) {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(structureType)
                beginStructure()
                beginStructureEntry() returns endEntryInfo
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)
                reader.beginRootTag()

                val parentPath = reader.getPath()
                reader.beginStructure()

                reader.beginStructureEntry() shouldBe endEntryInfo
                reader.getPath() shouldBe parentPath
            }
    }

    @Test
    fun beginning_an_end_structure_entry_should_change_path_back_to_the_parent() = withEachStructureType(1) {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(structureType)
                beginStructure()
                beginStructureEntry() returns firstEntryInfo
                beginStructureEntry() returns endEntryInfo
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)
                reader.beginRootTag()

                val parentPath = reader.getPath()
                reader.beginStructure()
                reader.beginStructureEntry()

                reader.beginStructureEntry() shouldBe endEntryInfo
                reader.getPath() shouldBe parentPath
            }
    }

    @Test
    fun ending_an_empty_structure_should_leave_path_unchanged() = withEachStructureType(0) {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(structureType)
                beginStructure()
                beginStructureEntry() returns endEntryInfo
                endStructure()
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)
                reader.beginRootTag()
                reader.beginStructure()
                reader.beginStructureEntry()

                val pathBeforeEndingTheStructure = reader.getPath()
                reader.endStructure()
                reader.getPath() shouldBe pathBeforeEndingTheStructure
            }
    }

    @Test
    fun ending_a_non_empty_structure_should_leave_path_unchanged() = withEachStructureType(1) {
        VerifyingNbtReaderMock
            .create {
                beginRootTag() returns RootTagInfo(structureType)
                beginStructure()
                beginStructureEntry() returns firstEntryInfo
                beginStructureEntry() returns firstEntryInfo
                beginStructureEntry() returns endEntryInfo
                endStructure()
            }
            .verify { delegate ->
                val reader = PathTrackingNbtReader(delegate)
                reader.beginRootTag()

                reader.beginStructure()
                reader.beginStructureEntry()
                reader.beginStructureEntry()
                reader.beginStructureEntry()

                val pathBeforeEndingTheStructure = reader.getPath()
                reader.endStructure()
                reader.getPath() shouldBe pathBeforeEndingTheStructure
            }
    }
}

private abstract class NbtReaderPrimitiveCalls<out T>(
    val primitiveType: NbtTagType,
) {
    abstract val primitiveReadResult: T

    abstract fun VerifyingNbtReaderMock.Builder.readPrimitive(): VerifyingMockFactory.Call<T>
    abstract fun NbtReader.readPrimitive(): T

    override fun toString(): String = "$primitiveType NbtReader calls"

    object ByteCalls : NbtReaderPrimitiveCalls<Byte>(TAG_Byte) {
        override val primitiveReadResult = 1234.toByte()

        override fun VerifyingNbtReaderMock.Builder.readPrimitive() = readByte()
        override fun NbtReader.readPrimitive() = readByte()
    }

    object ShortCalls : NbtReaderPrimitiveCalls<Short>(TAG_Short) {
        override val primitiveReadResult = 3456.toShort()

        override fun VerifyingNbtReaderMock.Builder.readPrimitive() = readShort()
        override fun NbtReader.readPrimitive() = readShort()
    }

    object IntCalls : NbtReaderPrimitiveCalls<Int>(TAG_Int) {
        override val primitiveReadResult = 5678

        override fun VerifyingNbtReaderMock.Builder.readPrimitive() = readInt()
        override fun NbtReader.readPrimitive() = readInt()
    }

    object LongCalls : NbtReaderPrimitiveCalls<Long>(TAG_Long) {
        override val primitiveReadResult = 7890L

        override fun VerifyingNbtReaderMock.Builder.readPrimitive() = readLong()
        override fun NbtReader.readPrimitive() = readLong()
    }

    object FloatCalls : NbtReaderPrimitiveCalls<Float>(TAG_Float) {
        override val primitiveReadResult = 3.14f.fix()

        override fun VerifyingNbtReaderMock.Builder.readPrimitive() = readFloat()
        override fun NbtReader.readPrimitive() = readFloat()
    }

    object DoubleCalls : NbtReaderPrimitiveCalls<Double>(TAG_Double) {
        override val primitiveReadResult = 2.71

        override fun VerifyingNbtReaderMock.Builder.readPrimitive() = readDouble()
        override fun NbtReader.readPrimitive() = readDouble()
    }

    object StringCalls : NbtReaderPrimitiveCalls<String>(TAG_String) {
        override val primitiveReadResult = "hello, string!"

        override fun VerifyingNbtReaderMock.Builder.readPrimitive() = readString()
        override fun NbtReader.readPrimitive() = readString()
    }
}

private abstract class NbtReaderStructureCalls<out TStructureInfo, out TEntryInfo>(
    val structureType: NbtTagType,
) {
    abstract val firstEntryInfo: TEntryInfo
    abstract val secondEntryInfo: TEntryInfo
    abstract val endEntryInfo: TEntryInfo

    abstract fun getEntryPathNode(entry: Int): NbtPath.Node

    abstract fun VerifyingNbtReaderMock.Builder.beginStructure()
    abstract fun VerifyingNbtReaderMock.Builder.beginStructureEntry(): VerifyingMockFactory.Call<TEntryInfo>
    abstract fun VerifyingNbtReaderMock.Builder.endStructure()

    abstract fun NbtReader.beginStructure(): TStructureInfo
    abstract fun NbtReader.beginStructureEntry(): TEntryInfo
    abstract fun NbtReader.endStructure()

    override fun toString(): String = "$structureType NbtReader calls"

    object Compound : NbtReaderStructureCalls<Unit, CompoundEntryInfo>(TAG_Compound) {
        override val firstEntryInfo = CompoundEntryInfo(TAG_Int, "first")
        override val secondEntryInfo = CompoundEntryInfo(TAG_String, "second")
        override val endEntryInfo = CompoundEntryInfo.End

        override fun getEntryPathNode(entry: Int): NbtPath.Node = when (entry) {
            0 -> NameNode(firstEntryInfo.name, firstEntryInfo.type)
            1 -> NameNode(secondEntryInfo.name, secondEntryInfo.type)
            else -> error("unexpected entry: $entry")
        }

        override fun VerifyingNbtReaderMock.Builder.beginStructure() = beginCompound()
        override fun VerifyingNbtReaderMock.Builder.beginStructureEntry() = beginCompoundEntry()
        override fun VerifyingNbtReaderMock.Builder.endStructure() = endCompound()

        override fun NbtReader.beginStructure() = beginCompound()
        override fun NbtReader.beginStructureEntry() = beginCompoundEntry()
        override fun NbtReader.endStructure() = endCompound()
    }

    class ListCalls(private val size: Int) : NbtReaderStructureCalls<ListInfo, Boolean>(TAG_List) {
        private val entryType = TAG_Short
        override val firstEntryInfo = true
        override val secondEntryInfo = true
        override val endEntryInfo = false

        override fun getEntryPathNode(entry: Int) = NbtPath.IndexNode(entry, entryType)

        override fun VerifyingNbtReaderMock.Builder.beginStructure() = beginList() returns ListInfo(entryType, size)
        override fun VerifyingNbtReaderMock.Builder.beginStructureEntry() = beginListEntry()
        override fun VerifyingNbtReaderMock.Builder.endStructure() = endList()

        override fun NbtReader.beginStructure() = beginList()
        override fun NbtReader.beginStructureEntry() = beginListEntry()
        override fun NbtReader.endStructure() = endList()
    }

    class ByteArrayCalls(private val size: Int) : NbtReaderStructureCalls<ArrayInfo, Boolean>(TAG_Byte_Array) {
        override val firstEntryInfo = true
        override val secondEntryInfo = true
        override val endEntryInfo = false

        override fun getEntryPathNode(entry: Int) = NbtPath.IndexNode(entry, TAG_Byte)

        override fun VerifyingNbtReaderMock.Builder.beginStructure() = beginByteArray() returns ArrayInfo(size)
        override fun VerifyingNbtReaderMock.Builder.beginStructureEntry() = beginByteArrayEntry()
        override fun VerifyingNbtReaderMock.Builder.endStructure() = endByteArray()

        override fun NbtReader.beginStructure() = beginByteArray()
        override fun NbtReader.beginStructureEntry() = beginByteArrayEntry()
        override fun NbtReader.endStructure() = endByteArray()
    }

    class IntArrayCalls(private val size: Int) : NbtReaderStructureCalls<ArrayInfo, Boolean>(TAG_Int_Array) {
        override val firstEntryInfo = true
        override val secondEntryInfo = true
        override val endEntryInfo = false

        override fun getEntryPathNode(entry: Int) = NbtPath.IndexNode(entry, TAG_Int)

        override fun VerifyingNbtReaderMock.Builder.beginStructure() = beginIntArray() returns ArrayInfo(size)
        override fun VerifyingNbtReaderMock.Builder.beginStructureEntry() = beginIntArrayEntry()
        override fun VerifyingNbtReaderMock.Builder.endStructure() = endIntArray()

        override fun NbtReader.beginStructure() = beginIntArray()
        override fun NbtReader.beginStructureEntry() = beginIntArrayEntry()
        override fun NbtReader.endStructure() = endIntArray()
    }

    class LongArrayCalls(private val size: Int) : NbtReaderStructureCalls<ArrayInfo, Boolean>(TAG_Long_Array) {
        override val firstEntryInfo = true
        override val secondEntryInfo = true
        override val endEntryInfo = false

        override fun getEntryPathNode(entry: Int) = NbtPath.IndexNode(entry, TAG_Long)

        override fun VerifyingNbtReaderMock.Builder.beginStructure() = beginLongArray() returns ArrayInfo(size)
        override fun VerifyingNbtReaderMock.Builder.beginStructureEntry() = beginLongArrayEntry()
        override fun VerifyingNbtReaderMock.Builder.endStructure() = endLongArray()

        override fun NbtReader.beginStructure() = beginLongArray()
        override fun NbtReader.beginStructureEntry() = beginLongArrayEntry()
        override fun NbtReader.endStructure() = endLongArray()
    }
}
