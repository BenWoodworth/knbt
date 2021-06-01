package net.benwoodworth.knbt.file

import data.*
import kotlinx.serialization.KSerializer
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtVariant
import net.benwoodworth.knbt.asSource
import net.benwoodworth.knbt.tag.NbtTag
import okio.Source

class NbtTestFile<T>(
    val description: String,
    val nbt: Nbt,
    val valueSerializer: KSerializer<T>,
    val value: T,
    val nbtTag: NbtTag,
    contentHex: String,
    val headerLength: Long = 0,
) {
    private val bytes = contentHex
        .filterNot { it.isWhitespace() }
        .chunked(2) { it.toString().toUByte(16).toByte() }
        .toByteArray()

    fun toByteArray(): ByteArray = bytes.copyOf()

    fun asSource(): Source = bytes.asSource()
}

val nbtFiles = listOf(
    NbtTestFile(
        "test.nbt (uncompressed)",
        Nbt {
            variant = NbtVariant.Java
        },
        TestNbt.serializer(), testClass, testTag,
        """
            0A000B68656C6C6F20776F726C640800046E616D65000942616E616E72616D6100
        """
    ),
    NbtTestFile(
        "test.nbt (compressed gzip)",
        Nbt {
            variant = NbtVariant.Java
            compression = NbtCompression.Gzip
        },
        TestNbt.serializer(), testClass, testTag,
        """
            1F8B080803FB03600203746573742D677A69702E6E627400E362E0CE48CDC9C95728CF2FCA49E16060C94BCC4D65E0744ACC4BCC2B4A
            CC4D64000077DA5C3A21000000
        """
    ),
    NbtTestFile(
        "test.nbt (compressed zlib)",
        Nbt {
            variant = NbtVariant.Java
            compression = NbtCompression.Zlib
        },
        TestNbt.serializer(), testClass, testTag,
        """
            78DAE362E0CE48CDC9C95728CF2FCA49E16060C94BCC4D65E0744ACC4BCC2B4ACC4D6400009CE809A9
        """
    ),
    NbtTestFile(
        "bigtest.nbt (uncompressed)",
        Nbt {
            variant = NbtVariant.Java
        },
        BigTestNbt.serializer(), bigTestClass, bigTestTag,
        """
            0A00054C6576656C0400086C6F6E67546573747FFFFFFFFFFFFFFF02000973686F7274546573747FFF08000A737472696E6754657374
            002948454C4C4F20574F524C4420544849532049532041205445535420535452494E4720C385C384C39621050009666C6F6174546573
            743EFF1832030007696E74546573747FFFFFFF0A00146E657374656420636F6D706F756E6420746573740A000368616D0800046E616D
            65000648616D70757305000576616C75653F400000000A00036567670800046E616D6500074567676265727405000576616C75653F00
            0000000009000F6C6973745465737420286C6F6E67290400000005000000000000000B000000000000000C000000000000000D000000
            000000000E000000000000000F0900136C697374546573742028636F6D706F756E64290A000000020800046E616D65000F436F6D706F
            756E642074616720233004000A637265617465642D6F6E000001265237D58D000800046E616D65000F436F6D706F756E642074616720
            233104000A637265617465642D6F6E000001265237D58D0001000862797465546573747F070065627974654172726179546573742028
            74686520666972737420313030302076616C756573206F6620286E2A6E2A3235352B6E2A3729253130302C207374617274696E672077
            697468206E3D302028302C2036322C2033342C2031362C20382C202E2E2E2929000003E8003E2210080A162C4C12462004564E505C0E
            2E5828024A3830323E54103A0A482C1A12142036561C502A0E60585A02183862320C54423A3C485E1A44145236241C1E2A4060265A34
            180662000C2242083C165E4C44465204244E1E5C402E2628344A0630003E2210080A162C4C12462004564E505C0E2E5828024A383032
            3E54103A0A482C1A12142036561C502A0E60585A02183862320C54423A3C485E1A44145236241C1E2A4060265A34180662000C224208
            3C165E4C44465204244E1E5C402E2628344A0630003E2210080A162C4C12462004564E505C0E2E5828024A3830323E54103A0A482C1A
            12142036561C502A0E60585A02183862320C54423A3C485E1A44145236241C1E2A4060265A34180662000C2242083C165E4C44465204
            244E1E5C402E2628344A0630003E2210080A162C4C12462004564E505C0E2E5828024A3830323E54103A0A482C1A12142036561C502A
            0E60585A02183862320C54423A3C485E1A44145236241C1E2A4060265A34180662000C2242083C165E4C44465204244E1E5C402E2628
            344A0630003E2210080A162C4C12462004564E505C0E2E5828024A3830323E54103A0A482C1A12142036561C502A0E60585A02183862
            320C54423A3C485E1A44145236241C1E2A4060265A34180662000C2242083C165E4C44465204244E1E5C402E2628344A0630003E2210
            080A162C4C12462004564E505C0E2E5828024A3830323E54103A0A482C1A12142036561C502A0E60585A02183862320C54423A3C485E
            1A44145236241C1E2A4060265A34180662000C2242083C165E4C44465204244E1E5C402E2628344A0630003E2210080A162C4C124620
            04564E505C0E2E5828024A3830323E54103A0A482C1A12142036561C502A0E60585A02183862320C54423A3C485E1A44145236241C1E
            2A4060265A34180662000C2242083C165E4C44465204244E1E5C402E2628344A0630003E2210080A162C4C12462004564E505C0E2E58
            28024A3830323E54103A0A482C1A12142036561C502A0E60585A02183862320C54423A3C485E1A44145236241C1E2A4060265A341806
            62000C2242083C165E4C44465204244E1E5C402E2628344A0630003E2210080A162C4C12462004564E505C0E2E5828024A3830323E54
            103A0A482C1A12142036561C502A0E60585A02183862320C54423A3C485E1A44145236241C1E2A4060265A34180662000C2242083C16
            5E4C44465204244E1E5C402E2628344A0630003E2210080A162C4C12462004564E505C0E2E5828024A3830323E54103A0A482C1A1214
            2036561C502A0E60585A02183862320C54423A3C485E1A44145236241C1E2A4060265A34180662000C2242083C165E4C44465204244E
            1E5C402E2628344A063006000A646F75626C65546573743FDF8F6BBBFF6A5E00
        """
    ),
    NbtTestFile(
        "bigtest.nbt (gzip compressed)",
        Nbt {
            variant = NbtVariant.Java
            compression = NbtCompression.Gzip
        },
        BigTestNbt.serializer(), bigTestClass, bigTestTag,
        """
            1F8B0800000000000000ED54CF4F1A41147EC202CB9682B1C41063CCABB584A5DBCD421189B188162C9A0D1AD8A83186B82BC3822EBB
            6677B0F1D44B7B6C7AEB3FD3237F43CFBDF6BFA0C32F7B69CFBDF032C9F7E6BD6FE67B6F2679020454724F2C0E78CBB14D8D78F4E370
            623E087B1DC7A593180F8247DDEE840262B5A2AAC778765C57CBA8550F1BC8D61E6A9586860DAD7E587B8F83CF834F836FCF03106E5B
            8E3EBEA5384C64FD10EADA74A62340DC662E69E1B5D3BB73FA760B29DB0BE0EFE83D1E385BEF110856F5DE5DDF0B40E05EB7FA64B704
            008C414C73C608554CD3202E7DA4C0C8C210B3BADE580B53A3EE448E450330B127538C4CF1E914A3538C85E1D99FE3B3F24481A57C33
            DDD8BBC7AA75135F281C08D72ED1593FAF1D1B602159DFFAF105FEC1CEFC9DBD00BCF140C9F885424046FE9EEBEA0F933A688760BBEB
            3237A3280A8EBBF5D06963CA4EDBE9ECE6E62B3BBD25BE6449093DAABB94FD187EE8D20EDA6F154CB1683E2BE19B9C8499BC84050965
            59164500FF2F28AE2FF2C2B2A42E1D20775A3BB98CCAE729DF5141C916B5C56DA12AAD2CC5317FBA7A928E5E9D5FF81205231BD1F6B7
            77AACD9572BC9EDF585D4B97AE9217B944D080C8FA3EBFB3DC54CB07756EA3B676599293A9DC5150996BCC35E61AFF57230842CBE91B
            D678C2ECFEFC7AFB7D78D384DFD4F2A4FB08060000
        """
    ),
    NbtTestFile(
        "bigtest.nbt (zlib compressed)",
        Nbt {
            variant = NbtVariant.Java
            compression = NbtCompression.Zlib
        },
        BigTestNbt.serializer(), bigTestClass, bigTestTag,
        """
            78DAED54CF4F1A41147EC202CB9682B1C41063CCABB584A5DBCD421189B188162C9A0D1AD8A83186B82BC3822EBB6677B0F1D44B7B6C
            7AEB3FD3237F43CFBDF6BFA0C32F7B69CFBDF032C9F7E6BD6FE67B6F2679020454724F2C0E78CBB14D8D78F4E370623E087B1DC7A593
            180F8247DDEE840262B5A2AAC778765C57CBA8550F1BC8D61E6A9586860DAD7E587B8F83CF834F836FCF03106E5B8E3EBEA5384C64FD
            10EADA74A62340DC662E69E1B5D3BB73FA760B29DB0BE0EFE83D1E385BEF110856F5DE5DDF0B40E05EB7FA64B704008C414C73C60855
            4CD3202E7DA4C0C8C210B3BADE580B53A3EE448E450330B127538C4CF1E914A3538C85E1D99FE3B3F24481A57C33DDD8BBC7AA75135F
            281C08D72ED1593FAF1D1B602159DFFAF105FEC1CEFC9DBD00BCF140C9F885424046FE9EEBEA0F933A688760BBEB3237A3280A8EBBF5
            D06963CA4EDBE9ECE6E62B3BBD25BE6449093DAABB94FD187EE8D20EDA6F154CB1683E2BE19B9C8499BC8405096559164500FF2F28AE
            2FF2C2B2A42E1D20775A3BB98CCAE729DF5141C916B5C56DA12AAD2CC5317FBA7A928E5E9D5FF81205231BD1F6B777AACD9572BC9EDF
            585D4B97AE9217B944D080C8FA3EBFB3DC54CB07756EA3B676599293A9DC5150996BCC35E61AFF57230842CBE91BD678C2ECFEFC7AFB
            7D78D384DFF684584F
        """
    ),
)
