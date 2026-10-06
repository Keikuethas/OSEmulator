package com.keikuethas.osemulator.cmd

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class VFS(
    val rootPath: String
) {

    open class VirtualContent(val parent: Directory? = null, val name: String) {
        open fun copy(parent: Directory? = this.parent, name: String = this.name) =
            if (this is Directory) Directory(parent, name, children)
            else VirtualContent(parent, name)
    }

    class Directory(
        parent: Directory? = null,
        name: String,
        initChildren: List<VirtualContent> = emptyList()
    ) : VirtualContent(parent, name) {

        private val _children: MutableList<VirtualContent> = initChildren.toMutableList()
        val children get() = _children.toList()

        override fun copy(parent: Directory?, name: String): Directory {
            val newDir = Directory(parent, name)
            _children.forEach { newDir._children.add(it.copy(parent = newDir)) }
            return newDir
        }

        fun getContent(path: String): VirtualContent? {
            var localResult: VirtualContent = this
            var mutPath = path
            while (mutPath.isNotBlank() && (localResult is Directory)) {
                val localPath = mutPath.takeWhile { it != '/' }

                if (localPath.isNotBlank())
                    when (localPath) {
                        "." -> {}
                        ".." -> localResult = localResult.parent ?: break
                        else -> localResult =
                            localResult.children.find { it.name == localPath }
                                ?: break
                    }

                mutPath = mutPath.drop(localPath.length + 1)
            }
            return if (mutPath.isBlank()) localResult else null
        }

        fun addFile(name: String): Boolean =
            if (children.any { it.name == name }) false
            else _children.add(VirtualContent(this, name))

        fun addDir(name: String): Boolean =
            if (children.any { it.name == name }) false
            else _children.add(Directory(this, name))

        fun addChild(child: VirtualContent): Boolean =
            if (children.any { it.name == child.name }) false
            else _children.add(child.copy(parent = this))

        val fullPath: String
            get() =
                if (parent != null) "${parent.fullPath}/$name"
                else name

        val pathTo: String
            get() =
                if (parent != null) "${parent.fullPath}/"
                else ""

    }

    companion object {
        fun makeDir(dir: File): Directory {
            require(dir.isDirectory) { "Путь не является директорией: ${dir.path}" }
            require(dir.canRead()) { "Нет прав на чтение ${dir.path}" }
            val localResult = Directory(name = dir.name)
            println(dir.name)
            println(dir.absolutePath)

            dir.listFiles().forEach { file ->

                localResult.addChild(
                    if (file.isDirectory) makeDir(file)
                    else VirtualContent(name = file.name)
                )
            }

            return localResult
        }
    }

    private val _curDir = MutableStateFlow(
        Directory(null, "initRoot")
    )

    private var absoluteRootPath: String = ""

    init {
        val candidate = File(rootPath)
        val resolved = if (candidate.isAbsolute) candidate
        else File("", rootPath)

        require(resolved.exists()) { "Не удалось найти путь: $rootPath" }
        require(resolved.isDirectory) { "Путь не является директорией: $rootPath" }

        _curDir.value = makeDir(resolved)
        absoluteRootPath = resolved.absolutePath
    }

    val curDir = _curDir.asStateFlow()

    fun getFileOnDisk(path: String): File =
        File(absoluteRootPath, "${_curDir.value.pathTo}/$path")

    val root: Directory
        get() {
            var localResult = curDir.value
            while (localResult.parent != null)
                localResult = localResult.parent
            return localResult
        }

    fun getContent(path: String): VirtualContent? =
        if (path.startsWith('/')) root.getContent(path.drop(1))
        else curDir.value.getContent(path)

    fun goTo(path: String): Boolean {
        val newDir = getContent(path) as? Directory
        return if (newDir != null) {
            _curDir.value = newDir
            true
        } else false
    }
}