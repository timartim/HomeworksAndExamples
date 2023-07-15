import XCTest
@testable import JobTracking

final class JobTrackingTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    // MARK: - Callback
    
    func testCallbackLinear() {
        let gcd = GCDJobTracker<Int, String, Error>(memoizing: .started) { key, completion in
            if key == 1 {
                completion(.success("One"))
            } else if key == 2 {
                completion(.success("Two"))
            } else {
                completion(.success("None"))
            }
        }
        
        var ans: String?
        
        gcd.startJob(for: 1) { result in
            do {
                ans = try result.get()
                XCTAssertEqual(ans, "One")
            } catch {}
        }
        
        gcd.startJob(for: 2) { result in
            do {
                ans = try result.get()
                XCTAssertEqual(ans, "Two")
            } catch {}
        }


        gcd.startJob(for: 3) { result in
            do {
                ans = try result.get()
                XCTAssertEqual(ans, "None")
            } catch {}
        }
    }
    
    func testCallbackOneByOne() {
        let gcd = GCDJobTracker<Int, String, Error>(memoizing: .started) { key, completion in
            if key == 1 {
                completion(.success("One"))
            } else if key == 2 {
                completion(.success("Two"))
            } else {
                completion(.success("None"))
            }
        }
        
        var ans: String?
        
        gcd.startJob(for: 1) { result in
            do {
                ans = try result.get()
                XCTAssertEqual(ans, "One")
            } catch {}
        }
        
        gcd.startJob(for: 1) { result in
            do {
                ans = try result.get()
                XCTAssertEqual(ans, "One")
            } catch {}
        }
        
        gcd.startJob(for: 1) { result in
            do {
                ans = try result.get()
                XCTAssertEqual(ans, "One")
            } catch {}
        }
    }
    
    func testCallbackFactorial() {
        let gcd = GCDJobTracker<UInt64, UInt64, Error>(memoizing: .started) { key, completion in
            var ans: UInt64 = 1
            for i: UInt64 in UInt64(2)...key {
                ans *= i
            }
            completion(.success(ans))
        }
        
        var ans: UInt64?
        
        gcd.startJob(for: 20) { result in
            do {
                ans = try result.get()
                XCTAssertEqual(ans, 2432902008176640000)
            } catch {}
        }
        gcd.startJob(for: 20) { result in
            do {
                ans = try result.get()
                XCTAssertEqual(ans, 2432902008176640000)
            } catch {}
        }
        gcd.startJob(for: 20) { result in
            do {
                ans = try result.get()
                XCTAssertEqual(ans, 2432902008176640000)
            } catch {}
        }
        
    }
    
    // MARK: - Concurrent
    
    func testConcurrentLinear() async throws {
        let con = ConcurrentJobTracker<Int, String, Error>(memoizing: .started) { key, completion in
            if key == 1 {
                completion(.success("One"))
            } else if key == 2 {
                completion(.success("Two"))
            } else {
                completion(.success("None"))
            }
        }
        
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 2))  // Two
        try await print(con.startJob(for: 3))  // None
    }
    
    func testConcurrentOneByOne() async throws {
        let con = ConcurrentJobTracker<Int, String, Error>(memoizing: .succeeded) { key, completion in
            if key == 1 {
                completion(.success("One"))
            } else if key == 2 {
                completion(.success("Two"))
            } else {
                completion(.success("None"))
            }
        }
        
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
        try await print(con.startJob(for: 1))  // One
    }
    
    func testConcurrentFactorial() async throws {
        let con = ConcurrentJobTracker<UInt64, UInt64, Error>(memoizing: .started) { key, completion in
            var ans: UInt64 = 1
            for j in 1...100000 {
                ans = UInt64(j)
                for i: UInt64 in UInt64(2)...key {
                    ans *= i
                }
            }
            completion(.success(ans))
        }
        
        async let t1 = con.startJob(for: 10)
        async let t2 = con.startJob(for: 10)
        async let t3 = con.startJob(for: 10)
        
        let ans = try await [t1, t2, t3]
        print(ans)
    }
    
    func testConcurrentFactorialLinear() async throws {
        let con = ConcurrentJobTracker<UInt64, UInt64, Error>(memoizing: .succeeded) { key, completion in
            var ans: UInt64 = 1
            for i: UInt64 in UInt64(2)...key {
                ans *= i
            }
            completion(.success(ans))
        }
        
        _ = try await con.startJob(for: 20)  // 2432902008176640000
        _ = try await con.startJob(for: 20)  // 2432902008176640000
        _ = try await con.startJob(for: 20)  // 2432902008176640000
    }
    
    // MARK: - Combine
    
    func testCombineFactorial() {
        let comb = CombineJobTracker<UInt64, UInt64, Error>(memoizing: .started) { key, completion in
            var ans: UInt64 = 1
            for i: UInt64 in UInt64(2)...key {
                ans *= i
            }
            completion(.success(ans))
        }
        
        _ = comb.startJob(for: 20).sink(receiveCompletion: { compl in }, receiveValue: { value in
            XCTAssertNotNil(value)
        })
        _ = comb.startJob(for: 20).sink(receiveCompletion: { compl in }, receiveValue: { value in
            XCTAssertNotNil(value)
        })
        _ = comb.startJob(for: 20).sink(receiveCompletion: { compl in }, receiveValue: { value in
            XCTAssertNotNil(value)
        })
        _ = comb.startJob(for: 20).sink(receiveCompletion: { compl in }, receiveValue: { value in
            XCTAssertNotNil(value)
        })
        _ = comb.startJob(for: 20).sink(receiveCompletion: { compl in }, receiveValue: { value in
            XCTAssertNotNil(value)
        })
    }
    
}
