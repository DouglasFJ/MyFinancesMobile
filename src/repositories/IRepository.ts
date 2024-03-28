export interface IRepository<T, ID>{

    save(model: T): Promise<T>;
    update(model: T): Promise<T>;
    delete(id: ID): Promise<void>;
    findById(id: ID): Promise<T>;
    findAll(params: {sort: string, limit: number}): Promise<T[]>;
    existsById(id: ID): Promise<boolean>;
    count(): Promise<number>;

}