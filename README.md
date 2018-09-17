# orphan-finder

[![Travis CI](https://travis-ci.org/gmethvin/orphan-finder.svg?branch=master)](https://travis-ci.org/gmethvin/orphan-finder) [![Maven](https://img.shields.io/maven-central/v/io.methvin/orphan-finder_2.12.6.svg)](https://mvnrepository.com/artifact/io.methvin/orphan-finder)

A Scala compiler plugin for finding orphan expressions in Scala code. An orphan expression of type `T` occurs when the result of an expression of type `T` is discarded. For example, an orphan `Future` occurs when a `Future` result is not assigned to a value, returned, or otherwise transformed. This can often indicate a bug in the code.

Currently, the implementation simply checks for statements within blocks matching the configured type(s). Note that this does not cover the cases already covered by the `-Ywarn-value-discard` compiler option.

The `demo` module in this repository shows an example of warnings emitted. The output looks something like:

```
[warn] /Users/greg/projects/orphan-finder/demo/src/main/scala/io/methvin/orphanfinder/Demo.scala:12:11: Orphan scala.concurrent.Future found!
[warn]     Future("hello")
[warn]           ^
[warn] /Users/greg/projects/orphan-finder/demo/src/main/scala/io/methvin/orphanfinder/Demo.scala:15:57: Orphan scala.concurrent.Future found!
[warn]     Future(throw new RuntimeException("hello")).recover {
[warn]                                                         ^
```

## Usage

To use this plugin, add the following to `build.sbt`:

```scala
inThisBuild(Seq(
  autoCompilerPlugins := true,
  addCompilerPlugin("io.methvin" %% "orphan-finder" % orphanFinderVersion cross CrossVersion.full),
  scalacOptions ++= Seq(
    // Configure the orphan types you want to warn about
    "-P:orphan-finder:class:scala.concurrent.Future"
  )
))
```

where `orphanFinderVersion` is the version you wish to use. The latest is: [![Maven](https://img.shields.io/maven-central/v/io.methvin/orphan-finder_2.12.6.svg)](https://mvnrepository.com/artifact/io.methvin/orphan-finder).
