
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.fileexplorer.FakeFileRepository
import com.example.fileexplorer.data.FileItem
import com.example.fileexplorer.viewModels.FileExplorerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
@ExperimentalCoroutinesApi
class FileExplorerViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var fakeFileRepository: FakeFileRepository

    private lateinit var viewModel: FileExplorerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeFileRepository = FakeFileRepository()

        val realSavedStateHandle = SavedStateHandle() // Real instance of SavedStateHandle
        fakeFileRepository = FakeFileRepository()
        viewModel = FileExplorerViewModel(realSavedStateHandle, fakeFileRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
    @Test
    fun testLoadFiles() {
        // Set up the expected behavior
        val expectedFiles = listOf(
            FileItem("file1.txt", "/path/file1.txt", false, 123456789, "txt", 1024),
            // ... other file items ...
        )
        fakeFileRepository.setFakeFiles(expectedFiles)

        // Call the method under test
        viewModel.loadFiles("/path")

        // Assertions
        assertEquals(expectedFiles, viewModel.fileItems.value)
    }

}

