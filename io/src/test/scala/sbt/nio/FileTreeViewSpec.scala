package sbt.nio

import java.nio.file._

import org.scalatest.FlatSpec
import sbt.io.IO
import sbt.nio.file.proposal.{ AnyPath, FileTreeView, Glob, RecursiveGlob }

class FileTreeViewSpec extends FlatSpec {
  val view = FileTreeView.default
  "FileTreeView" should "return the source root with depth == -1" in IO.withTemporaryDirectory {
    dir =>
      assert(
        view.list(dir.toPath.getParent).filter(_._1 == dir.toPath).map(_._1) == Seq(dir.toPath))
  }
  "FileTreeView" should "not return the source root with depth >= 0" in IO.withTemporaryDirectory {
    dir =>
      assert(view.list(Glob(dir, AnyPath / RecursiveGlob)).isEmpty)
  }
  "FileTreeView" should "get recursive files" in IO.withTemporaryDirectory { dir =>
    val subdir = Files.createDirectory(dir.toPath.resolve("subdir"))
    val nestedSubdir = Files.createDirectory(subdir.resolve("nested-subdir"))
    val file = Files.createFile(nestedSubdir.resolve("file"))
    assert(
      view.list(Glob(dir, RecursiveGlob)).collect { case (p, a) if !a.isDirectory => p } == Seq(
        file))
  }
}
