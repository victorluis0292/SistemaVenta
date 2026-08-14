package Controlador;

import Modelo.CatalogoGlobal;
import Modelo.CatalogoGlobalDao;
import Modelo.Combo;
import Modelo.Productos;
import Modelo.ProductosDao;
import Modelo.Proveedor;
import Modelo.ProveedorDao;

import java.util.List;

/**
 * Controlador del módulo de Productos.
 *
 * Se encarga de coordinar las operaciones entre:
 *
 * Vista -> ProductoController -> DAO
 */
public class ProductoController {

    private final int idEmpresa;

    private final ProductosDao proDao;
    private final ProveedorDao proveedorDao;
    private final CatalogoGlobalDao catalogoDao;

    public ProductoController(
            int idEmpresa,
            ProductosDao proDao,
            ProveedorDao proveedorDao) {

        this.idEmpresa = idEmpresa;
        this.proDao = proDao;
        this.proveedorDao = proveedorDao;
        this.catalogoDao = new CatalogoGlobalDao();
    }

    // ============================================================
    // PRODUCTOS
    // ============================================================

    public List<Productos> listarProductos() {

        return proDao.ListarProductos(idEmpresa);
    }

    public boolean guardarProducto(Productos producto) {

        producto.setId_empresa(idEmpresa);

        return proDao.RegistrarProductos(producto);
    }

    public boolean editarProducto(Productos producto) {

        producto.setId_empresa(idEmpresa);

        return proDao.ModificarProductos(producto);
    }

    public void eliminarProducto(int idProducto) {

        proDao.EliminarProductos(idProducto);
    }

    // ============================================================
    // VALIDACIONES
    // ============================================================

    public boolean codigoExiste(
            String codigo,
            String idActual) {

        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }

        List<Productos> productos =
                proDao.ListarProductos(idEmpresa);

        for (Productos producto : productos) {

            String codigoProducto =
                    producto.getCodigo();

            if (codigoProducto == null) {
                continue;
            }

            if (codigoProducto.equalsIgnoreCase(
                    codigo.trim())) {

                String idProducto =
                        String.valueOf(
                                producto.getId()
                        );

                if (idActual == null
                        || idActual.trim().isEmpty()
                        || !idProducto.equals(
                                idActual.trim())) {

                    return true;
                }
            }
        }

        return false;
    }

    // ============================================================
    // PROVEEDORES
    // ============================================================

    public List<Proveedor> listarProveedores() {

        return proveedorDao
                .ListarProveedorPorEmpresa(idEmpresa);
    }

    public boolean registrarProveedor(
            Proveedor proveedor) {

        proveedor.setIdEmpresa(idEmpresa);

        return proveedorDao
                .RegistrarProveedor(proveedor);
    }

    // ============================================================
    // CATALOGO GLOBAL
    // ============================================================

    public List<CatalogoGlobal> buscarCatalogoGlobal(
            String filtro) {

        if (filtro == null) {
            filtro = "";
        }

        return catalogoDao.buscarPorNombre(
                filtro.trim()
        );
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public int getIdEmpresa() {
        return idEmpresa;
    }
}