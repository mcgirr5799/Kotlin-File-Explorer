
import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
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

    private val context = ApplicationProvider.getApplicationContext<Context>()

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
        )
        fakeFileRepository.setFakeFiles(expectedFiles)

        // Call the method under test
        viewModel.loadFiles("/path")

        // Assertions
        assertEquals(expectedFiles, viewModel.fileItems.value)
    }

    @Test
    fun testLoadFiles_error() {
        // Set up the expected behavior
        val expectedFiles = emptyList<FileItem>()
        fakeFileRepository.setFakeFiles(expectedFiles)

        // Call the method under test
        viewModel.loadFiles("/path")

        // Assertions
        assertEquals(expectedFiles, viewModel.fileItems.value)
    }


    @Test
    fun testNavigateToDirectory() {
        // Set initial directory
        val initialPath = "/path/subpath"
        viewModel.navigateToDirectory(initialPath)

        // Expected directory
        val expectedPath = "/path/subpath"
        assertEquals(expectedPath, viewModel.currentDirectory.value)
    }

    @Test
    fun testNavigateUp() {
        // Set initial directory
        val initialPath = "/path/subpath"
        viewModel.navigateToDirectory(initialPath)

        // Perform the action
        viewModel.navigateUp()

        // Expected parent directory
        val parentPath = "/path"

        // Verify the current directory is updated to parent
        assertEquals(parentPath, viewModel.currentDirectory.value)
    }

    @Test
    fun testSortFiles() {
        // Prepare a list of files in random order
        val unsortedFiles = listOf(
            FileItem("b.txt", "/path/b.txt", false, 123456789, "txt", 2048),
            FileItem("a.txt", "/path/a.txt", false, 123456790, "txt", 1024)
        )
        fakeFileRepository.setFakeFiles(unsortedFiles)
        viewModel.loadFiles("/path")

        // Perform the action with different sort options
        viewModel.sortFiles("Name")
        val sortedByName = viewModel.fileItems.value
        viewModel.sortFiles("Date")
        val sortedByDate = viewModel.fileItems.value
        viewModel.sortFiles("Size")
        val sortedBySize = viewModel.fileItems.value

        // Assertions
        assertEquals(listOf(unsortedFiles[1], unsortedFiles[0]), sortedByName)
        assertEquals(listOf(unsortedFiles[1], unsortedFiles[0]), sortedByDate)
        assertEquals(listOf(unsortedFiles[0], unsortedFiles[1]), sortedBySize)
    }

    @Test
    fun testUpdateCurrentDirectoryFromUri() {
        // Prepare a Uri
        val uri = Uri.parse("content://com.example.fileexplorer/document/primary%3Apath%2Fsubpath")
        // Perform the action
        viewModel.updateCurrentDirectoryFromUri(uri)
        // Expected directory
        val expectedPath = "content://com.example.fileexplorer/document/primary%3Apath%2Fsubpath"
        // Verify the current directory is updated to parent
        assertEquals(expectedPath, viewModel.currentDirectory.value)
    }

}
