package dev.noyzys.jmh.setup

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@Fork(1, jvmArgsAppend = ["-XX:+UseG1GC", "-XX:+AlwaysPreTouch", "-Xms2G", "-Xmx2G"])
@Warmup(iterations = 15, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 15, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class Benchmarks {

    @Benchmark
    fun eagerException() = Exception()

    @Benchmark
    fun lazyException() = lazy { Exception() }.value

    @Benchmark
    fun uninitializedLazyException() = lazy { Exception() }

    @Benchmark
    fun emptyLazy() = lazy { }
}
