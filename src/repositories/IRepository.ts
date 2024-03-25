export interface IRepository<T, ID>{

    save(model: T): T;
    update(model: T): T;
    delete(id: ID): void;
    findById(id: ID): T;
    findAll(params: {sort: string, limit: number}): T[];
    existsById(id: ID): boolean;
    count(): number;

}